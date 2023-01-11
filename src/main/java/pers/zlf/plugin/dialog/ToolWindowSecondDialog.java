package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class ToolWindowSecondDialog extends JDialog {
    private String[] columnArr;
    private JPanel contentPane;
    private JButton submitButton;
    private JButton backButton;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private JRadioButton defaultTemplateRadioButton;
    private JRadioButton customTemplateRadioButton;
    private List<ColumnInfo> columnInfoList;

    public ToolWindowSecondDialog() {
        setContentPane(contentPane);
        setModal(true);

        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.addMouseListener(getListener(addButton, ICON.ADD2_PNG, ICON.ADD_PNG));
        addButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
            Object[] row = {columnArr[0], StringUtil.toHumpStyle(columnArr[0]), COMMON.SELECT_OPTIONS[0]};
            int rowNum = model.getRowCount();
            model.insertRow(rowNum, row);
            columnTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(columnArr)));
            columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
            columnTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(COMMON.SELECT_OPTIONS)));
        });

        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addMouseListener(getListener(deleteButton, ICON.DELETE2_PNG, ICON.DELETE_PNG));
        deleteButton.addActionListener(e -> {
            int rowNum = columnTable.getSelectedRow();
            if (rowNum >= 0) {
                DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
                model.removeRow(rowNum);
            }
        });
    }

    public void initColumn(TableInfo tableInfo) {
        columnInfoList = tableInfo.getColumnList();
        int columnCount = columnInfoList.size();
        columnArr = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnArr[i] = columnInfoList.get(i).getSqlColumnName();
        }
        columnTable.setModel(new DefaultTableModel(null, COMMON.QUERY_COLUMN_TABLE_HEADER));
        columnTable.getModel().addTableModelListener(e -> {
            int columnNum = e.getColumn();
            if (columnNum == 0) {
                DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
                model.setValueAt(StringUtil.toHumpStyle(model.getValueAt(e.getFirstRow(), 0).toString()), e.getFirstRow(), 1);
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
        DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
        int rowCount = model.getRowCount();
        if (rowCount > 0) {
            Map<String, ColumnInfo> columnInfoMap = columnInfoList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
            for (int i = 0; i < rowCount; i++) {
                String columnName = StringUtil.toString(model.getValueAt(i, 0));
                ColumnInfo queryColumnInfo = new ColumnInfo(columnName, model.getValueAt(i, 1), model.getValueAt(i, 2));
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

    private MouseListener getListener(JButton button, Icon mouseEnteredIcon, Icon mouseExitedIcon) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(mouseEnteredIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(mouseExitedIcon);
            }
        };
    }
}
