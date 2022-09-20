package dialog;

import com.intellij.ui.JBColor;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ToolWindowDialog extends JDialog {
    private JPanel contentPane;
    private JButton nextButton;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField projectNameField;
    private JTextField packagePathField;

    public ToolWindowDialog() {
        projectNameField.setForeground(JBColor.GRAY);
        projectNameField.setText(COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER);
        packagePathField.setForeground(JBColor.GRAY);
        packagePathField.setText(COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER);
        textArea.setForeground(JBColor.GRAY);
        textArea.setText(COMMON_CONSTANT.TEXT_AREA_PLACEHOLDER);
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

        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON_CONSTANT.TEXT_AREA_PLACEHOLDER.equals(textArea.getText())) {
                    textArea.setText(COMMON_CONSTANT.BLANK_STRING);
                    textArea.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(textArea.getText())) {
                    textArea.setForeground(JBColor.GRAY);
                    textArea.setText(COMMON_CONSTANT.TEXT_AREA_PLACEHOLDER);
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

    public JTextField getAuthorField() {
        return authorField;
    }

    public JTextField getProjectNameField() {
        return projectNameField;
    }

    public JTextField getPackagePathField() {
        return packagePathField;
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
