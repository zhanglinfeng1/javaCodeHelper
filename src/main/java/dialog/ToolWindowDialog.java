package dialog;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import constant.COMMON_CONSTANT;
import service.CreateFileService;
import util.StringUtil;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ToolWindowDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField projectNameField;
    private JTextField packagePathField;

    public ToolWindowDialog() {
        projectNameField.setForeground(JBColor.GRAY);
        projectNameField.setText(COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER);
        setContentPane(contentPane);
        setModal(true);
        buttonOK.addActionListener(e -> {
            try {
                new CreateFileService().createFile(authorField.getText(), projectNameField.getText(), packagePathField.getText(), textArea.getText());
                Messages.showMessageDialog(COMMON_CONSTANT.SUCCESS, COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                ex.printStackTrace();
                Messages.showMessageDialog(COMMON_CONSTANT.FAIL, COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            }
        });

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
    }

    public JPanel getContent() {
        return this.contentPane;
    }
}
