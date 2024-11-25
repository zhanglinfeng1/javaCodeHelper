package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.util.EscapeUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2024/11/25 10:12
 */
public class EscapeDialog {
    private JTextArea upTextArea;
    private JButton downButton;
    private JButton upButton;
    private JTextArea downTextArea;
    private JPanel contentPanel;

    public EscapeDialog() {
        downButton.addActionListener(e -> downTextArea.setText(EscapeUtil.escape(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(EscapeUtil.unescape(downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
