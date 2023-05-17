package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;
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
 * @Author zhanglinfeng
 * @Date create in 2022/9/20 10:33
 */
public class ToolWindowSecondDialog extends BaseDialog {
    private JPanel contentPane;
    private JButton submitButton;
    private JButton backButton;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private JRadioButton defaultTemplateRadioButton;
    private JRadioButton customTemplateRadioButton;
    private List<ColumnInfo> columnInfoList;
    private String[] columnArr;
    private final DefaultTableModel defaultTableModel;

    public ToolWindowSecondDialog() {
        columnTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultTableModel = new DefaultTableModel(null, COMMON.QUERY_COLUMN_TABLE_HEADER);
        columnTable.setModel(defaultTableModel);

        addButton.addActionListener(e -> {
            defaultTableModel.addRow(new String[]{columnArr[0], StringUtil.toHumpStyle(columnArr[0]), COMMON.SELECT_OPTIONS[0]});
            columnTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(columnArr)));
            columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
            columnTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(COMMON.SELECT_OPTIONS)));
        });
        deleteButton.addActionListener(e -> Equals.of(columnTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            defaultTableModel.removeRow(rowNum);
            if (columnTable.getRowCount() == 0) {
                removeMouseListener(deleteButton, ICON_ENUM.REMOVE);
            }
        }));
    }

    public void initColumn(TableInfo tableInfo) {
        columnInfoList = tableInfo.getColumnList();
        columnArr = columnInfoList.stream().map(ColumnInfo::getSqlColumnName).toArray(String[]::new);
        addMouseListener(addButton, ICON_ENUM.ADD);
        if (columnTable.getRowCount() == 0) {
            removeMouseListener(deleteButton, ICON_ENUM.REMOVE);
        }else {
            addMouseListener(deleteButton, ICON_ENUM.REMOVE);
        }
        defaultTableModel.getDataVector().clear();
        columnTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                String value = StringUtil.toHumpStyle(defaultTableModel.getValueAt(e.getFirstRow(), 0).toString());
                defaultTableModel.setValueAt(value, e.getFirstRow(), 1);
            }
        });
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public JPanel getContent() {
        return this.contentPane;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public List<ColumnInfo> getQueryColumnList() {
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

    public boolean useDefaultTemplate() {
        return defaultTemplateRadioButton.isSelected();
    }

    public void clearTableContent(){
        defaultTableModel.getDataVector().clear();
    }
}
