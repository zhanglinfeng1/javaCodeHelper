package pers.zlf.plugin.dialog;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/20 10:33
 */
public class CommonToolsDialog extends BaseDialog {
    private JPanel contentPane;
    private JButton button1;
    private JTextArea upTextArea;
    private JTextArea downTextArea;
    private JButton upButton;
    private JButton downButton;


    public CommonToolsDialog() {

    }


    public JPanel getContent() {
        return this.contentPane;
    }


}
