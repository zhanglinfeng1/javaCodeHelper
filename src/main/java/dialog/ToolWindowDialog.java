package dialog;

import com.intellij.openapi.ui.Messages;
import constant.COMMON_CONSTANT;
import util.CreateFileUtil;

import javax.swing.*;

public class ToolWindowDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea;

    public ToolWindowDialog() {
        setContentPane(contentPane);
        setModal(true);
        buttonOK.addActionListener(e -> {
            try {
                new CreateFileUtil().createFile(textArea.getText());
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
