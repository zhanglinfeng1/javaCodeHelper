package dialog;

import com.intellij.openapi.ui.Messages;
import constant.COMMON_CONSTANT;
import service.CreateFileService;

import javax.swing.*;

public class ToolWindowDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField modularNameField;
    private JTextField packagePathField;

    public ToolWindowDialog() {
        setContentPane(contentPane);
        setModal(true);
        buttonOK.addActionListener(e -> {
            try {
                new CreateFileService().createFile(authorField.getText(), modularNameField.getText(),packagePathField.getText(),textArea.getText());
                Messages.showMessageDialog(COMMON_CONSTANT.SUCCESS, "", Messages.getInformationIcon());
            } catch (Exception ex) {
                ex.printStackTrace();
                Messages.showMessageDialog(COMMON_CONSTANT.FAIL, "", Messages.getInformationIcon());
            }
        });
    }

    public JPanel getContent() {
        return this.contentPane;
    }

}
