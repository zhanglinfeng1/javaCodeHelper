package dialog;

import com.intellij.ui.table.JBTable;
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import pojo.ColumnInfo;
import pojo.TableInfo;
import util.StringUtil;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToolWindowSecondDialog extends JDialog {
    private String[] columnArr;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton backButton;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private List<ColumnInfo> columnInfoList;

    public ToolWindowSecondDialog() {
        setContentPane(contentPane);
        setModal(true);

        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.addMouseListener(new MouseListener() {
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
                addButton.setIcon(ICON_CONSTANT.ADD2_PNG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addButton.setIcon(ICON_CONSTANT.ADD_PNG);
            }
        });
        addButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
            Object[] row = {columnArr[0], COMMON_CONSTANT.BLANK_STRING, COMMON_CONSTANT.SELECT_OPTIONS[0]};
            model.insertRow(model.getRowCount(), row);
            JComboBox<String> columnComboBox = new JComboBox<>(columnArr);
            columnComboBox.setSelectedIndex(0);
            columnTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(columnComboBox));
            columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
            JComboBox<String> selectOptionsComboBox = new JComboBox<>(COMMON_CONSTANT.SELECT_OPTIONS);
            selectOptionsComboBox.setSelectedIndex(0);
            columnTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(COMMON_CONSTANT.SELECT_OPTIONS)));
        });

        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addMouseListener(new MouseListener() {
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
                deleteButton.setIcon(ICON_CONSTANT.DELETE2_PNG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setIcon(ICON_CONSTANT.DELETE_PNG);
            }
        });

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
        columnTable.setModel(new DefaultTableModel(null, COMMON_CONSTANT.QUERY_COLUMN_TABLE_HEADER));
    }

    public JButton getButtonOK() {
        return buttonOK;
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
                ColumnInfo sqlColumnInfo = columnInfoMap.get(columnName);
                if (null != sqlColumnInfo) {
                    queryColumnInfo.setSqlColumnType(sqlColumnInfo.getSqlColumnType());
                    queryColumnInfo.setColumnType(sqlColumnInfo.getColumnType());
                }
                queryColumnList.add(queryColumnInfo);
            }
        }
        return queryColumnList;
    }

}
