package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/28 13:50
 */
public class UnicodeDialog {
    private JPanel contentPanel;
    private JButton downButton;
    private JButton upButton;
    private JTextArea upTextArea;
    private JTextArea downTextArea;

    public UnicodeDialog() {
        downButton.addActionListener(e -> downTextArea.setText(StringUtil.unicodeEncode(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(StringUtil.unicodeDecode(downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
