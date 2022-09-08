package dialog;

import javax.swing.*;

public class ToolWindowDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea;

    public ToolWindowDialog() {
        setContentPane(contentPane);
        setModal(true);
        buttonOK.addActionListener(e -> {
            System.out.println(textArea.getText());
        });
    }

    public JPanel getContent() {
        return this.contentPane;
    }

}
