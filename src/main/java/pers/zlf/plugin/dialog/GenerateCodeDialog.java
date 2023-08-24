package pers.zlf.plugin.dialog;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.dialog.database.BaseSqlParse;
import pers.zlf.plugin.dialog.database.MysqlParse;
import pers.zlf.plugin.dialog.database.OracleParse;
import pers.zlf.plugin.dialog.database.PostgresqlParse;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 10:33
 */
public class GenerateCodeDialog extends BaseDialog {
    /** 主面板 */
    private JPanel contentPane;
    /** 第一面板 */
    private JPanel firstPanel;
    private JButton nextButton;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField fullPathField;
    private JTextField packagePathField;
    private JComboBox<String> dataBaseType;
    /** 第二面板 */
    private JPanel secondPanel;
    private JButton submitButton;
    private JButton backButton;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private JRadioButton defaultTemplateRadioButton;
    private JRadioButton customTemplateRadioButton;
    private List<ColumnInfo> columnInfoList;
    private String[] columnArr;

    private final Map<String, BaseSqlParse> sqlParseMap = new HashMap<>() {{
        put("mysql", new MysqlParse());
        put("oracle", new OracleParse());
        put("postgresql", new PostgresqlParse());
    }};

    public GenerateCodeDialog() {
        //初始化第一面板
        secondPanel.setVisible(false);
        fullPathField.setForeground(JBColor.GRAY);
        fullPathField.setText(Common.FULL_PATH_INPUT_PLACEHOLDER);
        packagePathField.setForeground(JBColor.GRAY);
        packagePathField.setText(Common.PACKAGR_PATH_INPUT_PLACEHOLDER);
        addFocusListener(fullPathField, Common.FULL_PATH_INPUT_PLACEHOLDER);
        addFocusListener(packagePathField, Common.PACKAGR_PATH_INPUT_PLACEHOLDER);
        initFirstPanelButtonListener();

        //初始化第二面板
        columnTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultTableModel = new DefaultTableModel(null, Common.QUERY_COLUMN_TABLE_HEADER);
        columnTable.setModel(defaultTableModel);
        initSecondPanelButtonListener();
    }

    public JPanel getContent() {
        return contentPane;
    }

    private void initFirstPanelButtonListener() {
        //下一步
        nextButton.addActionListener(e -> {
            try {
                //解析sql
                BaseSqlParse baseSqlParse = Optional.ofNullable(dataBaseType.getSelectedItem()).map(StringUtil::toString).map(sqlParseMap::get)
                        .orElseThrow(() -> new Exception("Database not support"));
                String sqlStr = Empty.of(textArea.getText()).ifEmptyThrow(() -> new Exception("Sql is not null"));
                TableInfo tableInfo = baseSqlParse.parseSql(sqlStr);
                tableInfo.setAuthor(authorField.getText());
                //初始化文件路径
                TemplateFactory.getInstance().init(getFullPath(), getPackagePathField(), tableInfo);
                showSecondPanel(tableInfo);
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    private void initSecondPanelButtonListener() {
        //添加
        addButton.addActionListener(e -> {
            addMouseListener(deleteButton, IconEnum.REMOVE);
            defaultTableModel.addRow(new String[]{columnArr[0], StringUtil.toHumpStyle(columnArr[0]), Common.SELECT_OPTIONS[0]});
            columnTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(columnArr)));
            columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
            columnTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(Common.SELECT_OPTIONS)));
        });
        //删除
        deleteButton.addActionListener(e -> Equals.of(columnTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            defaultTableModel.removeRow(rowNum);
            if (columnTable.getRowCount() == 0) {
                removeMouseListener(deleteButton, IconEnum.REMOVE);
            }
        }));
        //上一步
        backButton.addActionListener(e -> showFirstPanel());
        //生成代码
        submitButton.addActionListener(e -> {
            try {
                TemplateFactory.getInstance().create(getQueryColumnList(), defaultTemplateRadioButton.isSelected());
                clearTableContent();
                showFirstPanel();
                Messages.showMessageDialog(Common.SUCCESS, Common.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    private String getFullPath() throws Exception {
        return Equals.of(fullPathField.getText()).and(Common.FULL_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                .ifTrueThrow(() -> new Exception("Full path is not null"));
    }

    private String getPackagePathField() throws Exception {
        return Equals.of(packagePathField.getText()).and(Common.PACKAGR_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                .ifTrueThrow(() -> new Exception("Package is not null"));
    }

    private void showFirstPanel() {
        firstPanel.setVisible(true);
        secondPanel.setVisible(false);
    }

    private void showSecondPanel(TableInfo tableInfo) {
        firstPanel.setVisible(false);
        columnInfoList = tableInfo.getColumnList();
        columnArr = columnInfoList.stream().map(ColumnInfo::getSqlColumnName).toArray(String[]::new);
        addMouseListener(addButton, IconEnum.ADD);
        removeMouseListener(deleteButton, IconEnum.REMOVE);
        defaultTableModel.getDataVector().clear();
        columnTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                String value = StringUtil.toHumpStyle(defaultTableModel.getValueAt(e.getFirstRow(), 0).toString());
                defaultTableModel.setValueAt(value, e.getFirstRow(), 1);
            }
        });
        secondPanel.setVisible(true);
    }

    private List<ColumnInfo> getQueryColumnList() {
        List<ColumnInfo> queryColumnList = new ArrayList<>();
        int rowCount = defaultTableModel.getRowCount();
        if (rowCount > 0) {
            Map<String, ColumnInfo> columnInfoMap = columnInfoList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
            for (int i = 0; i < rowCount; i++) {
                String columnName = StringUtil.toString(defaultTableModel.getValueAt(i, 0));
                ColumnInfo queryColumnInfo = new ColumnInfo(columnName, defaultTableModel.getValueAt(i, 1), defaultTableModel.getValueAt(i, 2));
                Optional.ofNullable(columnInfoMap.get(columnName)).ifPresent(t -> {
                    queryColumnInfo.setSqlColumnType(t.getSqlColumnType());
                    queryColumnInfo.setColumnType(t.getColumnType());
                });
                queryColumnList.add(queryColumnInfo);
            }
        }
        return queryColumnList;
    }

}
