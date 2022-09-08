package dialog;

import util.CreateFileUtil;

import javax.swing.*;
import java.io.IOException;

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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public JPanel getContent() {
        return this.contentPane;
    }

}
