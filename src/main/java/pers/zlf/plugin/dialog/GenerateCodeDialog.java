package pers.zlf.plugin.dialog;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.database.DBTableParse;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
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
    private JPanel contentPane;
    private JTextField authorField;
    private TextFieldWithBrowseButton fullPathField;
    private JTextField packagePathField;
    private JButton submitButton;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private JRadioButton defaultTemplateRadioButton;
    private JRadioButton customTemplateRadioButton;
    private final List<ColumnInfo> columnInfoList;
    private final String[] columnArr;
    private final TableInfo tableInfo;

    public GenerateCodeDialog(DbTable dbTable) {
        //文本框初始化
        fullPathField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)));
        fullPathField.getTextField().setForeground(JBColor.GRAY);
        fullPathField.setText(Common.FULL_PATH_INPUT_PLACEHOLDER);
        packagePathField.setForeground(JBColor.GRAY);
        packagePathField.setText(Common.PACKAGR_PATH_INPUT_PLACEHOLDER);
        addFocusListener(fullPathField.getTextField(), Common.FULL_PATH_INPUT_PLACEHOLDER, true);
        addFocusListener(packagePathField, Common.PACKAGR_PATH_INPUT_PLACEHOLDER);
        //解析表结构
        tableInfo = new DBTableParse().parseSql(dbTable);
        //表格初始化
        columnTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultTableModel = new DefaultTableModel(null, Common.QUERY_COLUMN_TABLE_HEADER);
        columnTable.setModel(defaultTableModel);
        columnInfoList = tableInfo.getColumnList();
        columnArr = columnInfoList.stream().map(ColumnInfo::getSqlColumnName).toArray(String[]::new);
        defaultTableModel.getDataVector().clear();
        columnTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                String value = StringUtil.toHumpStyle(defaultTableModel.getValueAt(e.getFirstRow(), 0).toString());
                defaultTableModel.setValueAt(value, e.getFirstRow(), 1);
            }
        });
        //初始化按钮
        initButtonListener();

        this.setContentPane(contentPane);
        String title = Empty.of(dbTable.getComment()).map(t -> Common.SPACE + t).orElse(Common.BLANK_STRING) + dbTable.getName();
        this.setTitle(title);
        this.setModalityType(ModalityType.TOOLKIT_MODAL);
    }

    private void initButtonListener() {
        //初始化按钮背景色
        initButtonBackground(addButton, deleteButton);
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
        //生成代码
        submitButton.addActionListener(e -> {
            try {
                String fullPath = Equals.of(fullPathField.getText()).and(Common.FULL_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                        .ifTrueThrow(() -> new Exception(Message.FULL_PATH_NOT_NULL));
                String packagePath = Equals.of(packagePathField.getText()).and(Common.PACKAGR_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                        .ifTrueThrow(() -> new Exception(Message.PACKAGE_PATH_NOT_NULL));
                tableInfo.setAuthor(authorField.getText());
                tableInfo.setPackagePath(packagePath);
                tableInfo.setQueryColumnList(getQueryColumnList());
                //生成文件
                TemplateFactory.getInstance().create(fullPath, tableInfo, defaultTemplateRadioButton.isSelected());
                Messages.showMessageDialog(Common.SUCCESS, Common.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
        addMouseListener(addButton, IconEnum.ADD);
        removeMouseListener(deleteButton, IconEnum.REMOVE);
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
