package dialog;

import constant.COMMON_CONSTANT;
import pojo.ColumnInfo;
import pojo.TableInfo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolWindowSecondDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton backButton;
    private JPanel panel;
    private JTextField customTemplatesPathField;

    public ToolWindowSecondDialog() {
        setContentPane(contentPane);
        setModal(true);
    }

    public void initColumn(TableInfo tableInfo) {
        panel.removeAll();
        List<ColumnInfo> columnInfoList = tableInfo.getColumnList();
        panel.setLayout(new GridLayout(columnInfoList.size() + 1, 3));
        panel.add(new JLabel("字段名"));
        panel.add(new JLabel("是否作为查询条件"));
        panel.add(new JLabel("查询方式"));
        for (ColumnInfo columnInfo : columnInfoList) {
            panel.add(new JLabel(columnInfo.getSqlColumnName()));
            panel.add(new JRadioButton());
            panel.add(new JComboBox<>(COMMON_CONSTANT.SELECT_OPTIONS));
        }
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
        Component[] components = panel.getComponents();
        int length = components.length;
        if (length > 3) {
            for (int i = 3; i < length; i = i + 3) {
                JRadioButton jRadioButton = (JRadioButton) components[i + 1];
                if (jRadioButton.isSelected()) {
                    JLabel jLabel = (JLabel) components[i];
                    JComboBox jComboBox = (JComboBox) components[i + 2];
                    String sqlColumnName = jLabel.getText();
                    queryColumnList.add(new ColumnInfo(sqlColumnName, Objects.requireNonNull(jComboBox.getSelectedItem()).toString()));
                }
            }
        }
        return queryColumnList;
    }

    public String getCustomTemplatesPath(){
        return this.customTemplatesPathField.getText();
    }
}
