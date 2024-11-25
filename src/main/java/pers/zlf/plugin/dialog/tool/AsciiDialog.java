package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2023/10/23 15:48
 */
public class AsciiDialog {
    private JPanel contentPanel;
    private JTextArea upTextArea;
    private JButton downButton;
    private JButton upButton;
    private JTextArea downTextArea;

    public AsciiDialog() {
        downButton.addActionListener(e -> downTextArea.setText(StringUtil.toAscii(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(StringUtil.asciiToString(downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
