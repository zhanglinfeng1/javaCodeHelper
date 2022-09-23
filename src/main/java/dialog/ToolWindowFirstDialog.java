package dialog;

import com.intellij.ui.JBColor;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.Objects;

public class ToolWindowFirstDialog extends JDialog {
    private JPanel contentPane;
    private JButton nextButton;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField projectNameField;
    private JTextField packagePathField;
    private JComboBox<String> dataBaseType;

    public ToolWindowFirstDialog() {
        projectNameField.setForeground(JBColor.GRAY);
        projectNameField.setText(COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER);
        packagePathField.setForeground(JBColor.GRAY);
        packagePathField.setText(COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER);
        Arrays.stream(COMMON_CONSTANT.DATA_BASE_TYPE_OPTIONS).forEach(s->dataBaseType.addItem(s));
        setContentPane(contentPane);
        setModal(true);

        projectNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER.equals(projectNameField.getText())) {
                    projectNameField.setText(COMMON_CONSTANT.BLANK_STRING);
                    projectNameField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(projectNameField.getText())) {
                    projectNameField.setForeground(JBColor.GRAY);
                    projectNameField.setText(COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER);
                }
            }
        });

        packagePathField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER.equals(packagePathField.getText())) {
                    packagePathField.setText(COMMON_CONSTANT.BLANK_STRING);
                    packagePathField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(packagePathField.getText())) {
                    packagePathField.setForeground(JBColor.GRAY);
                    packagePathField.setText(COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER);
                }
            }
        });
    }

    public JPanel getContent() {
        return this.contentPane;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public String getProjectName() {
        String projectName = projectNameField.getText();
        if (COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER.equals(projectName)) {
            projectName = COMMON_CONSTANT.BLANK_STRING;
        }
        return projectName;
    }

    public String getPackagePathField() throws Exception {
        String packagePath = packagePathField.getText();
        if (COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER.equals(packagePath) || StringUtil.isEmpty(packagePath)) {
            throw new Exception("包路径不能为空");
        }
        return packagePath;
    }

    public String getSqlStr() throws Exception {
        String sqlStr = textArea.getText();
        if (StringUtil.isEmpty(sqlStr)) {
            throw new Exception("Sql不能为空");
        }
        return sqlStr;
    }

    public String getDataBaseType() {
        return Objects.requireNonNull(dataBaseType.getSelectedItem()).toString();
    }
}
