package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.ToolEnum;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/28 13:50
 */
public class SimpleToolDialog {
    private JPanel contentPanel;
    private JButton downButton;
    private JButton upButton;
    private JTextArea upTextArea;
    private JTextArea downTextArea;

    public SimpleToolDialog(ToolEnum toolEnum) {
        downButton.setText(toolEnum.downButtonName);
        upButton.setText(toolEnum.upButtonName);
        downButton.addActionListener(e -> downTextArea.setText(toolEnum.downButtonFunction.apply(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(toolEnum.upButtonFunction.apply(downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
