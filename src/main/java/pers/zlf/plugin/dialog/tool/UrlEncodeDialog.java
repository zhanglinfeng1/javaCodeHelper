package pers.zlf.plugin.dialog.tool;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/28 13:50
 */
public class UrlEncodeDialog {
    private JPanel contentPanel;
    private JButton downButton;
    private JButton upButton;
    private JTextArea upTextArea;
    private JTextArea downTextArea;

    public UrlEncodeDialog() {
        downButton.addActionListener(e -> downTextArea.setText(URLEncoder.encode(upTextArea.getText(), StandardCharsets.UTF_8)));
        upButton.addActionListener(e -> upTextArea.setText(URLDecoder.decode(downTextArea.getText(), StandardCharsets.UTF_8)));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
