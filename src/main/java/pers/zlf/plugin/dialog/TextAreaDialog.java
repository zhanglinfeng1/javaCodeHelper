package pers.zlf.plugin.dialog;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2025/1/6 17:40
 */
public class TextAreaDialog {
    private JPanel contentPanel;
    private JTextArea textArea;

    public TextAreaDialog(String text) {
        textArea.setText(text);
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
