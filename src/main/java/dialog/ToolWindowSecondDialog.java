package dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import factory.TemplateFactory;
import freemarker.template.Template;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ToolWindowSecondDialog extends JDialog {
    private String[] columnArr;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton backButton;
    private JTextField customTemplatesPathField;
    private JBTable columnTable;
    private JButton addButton;
    private JButton deleteButton;
    private JButton downloadButton;

    public ToolWindowSecondDialog() {
        setContentPane(contentPane);
        setModal(true);

        customTemplatesPathField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON_CONSTANT.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(customTemplatesPathField.getText())) {
                    customTemplatesPathField.setText(COMMON_CONSTANT.BLANK_STRING);
                    customTemplatesPathField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(customTemplatesPathField.getText())) {
                    customTemplatesPathField.setForeground(JBColor.GRAY);
                    customTemplatesPathField.setText(COMMON_CONSTANT.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
                }
            }
        });

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
            Object[] row = {columnArr[0], "", COMMON_CONSTANT.SELECT_OPTIONS[0]};
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

        downloadButton.addActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
            if (virtualFile != null) {
                String path = virtualFile.getPath();
                try {
                    List<Template> defaultTemplateList = TemplateFactory.getInstance().getDefaultTemplateList();
                    for (Template template : defaultTemplateList) {
                        FileWriter file = new FileWriter(path + COMMON_CONSTANT.DOUBLE_BACKSLASH + template.getName(), true);
                        //TODO 寻找替换方法
                        file.append(template.getRootTreeNode().toString());
                        file.flush();
                        file.close();
                    }
                } catch (Exception ex) {
                    Messages.showMessageDialog(ex.getMessage(), COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
                }
            }
        });
    }

    public void initColumn(TableInfo tableInfo) {
        customTemplatesPathField.setForeground(JBColor.GRAY);
        customTemplatesPathField.setText(COMMON_CONSTANT.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        List<ColumnInfo> columnInfoList = tableInfo.getColumnList();
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
            for (int i = 0; i < rowCount; i++) {
                queryColumnList.add(new ColumnInfo(model.getValueAt(i, 0), model.getValueAt(i, 1), model.getValueAt(i, 2)));
            }
        }
        return queryColumnList;
    }

    public String getCustomTemplatesPath() {
        return this.customTemplatesPathField.getText();
    }

}
