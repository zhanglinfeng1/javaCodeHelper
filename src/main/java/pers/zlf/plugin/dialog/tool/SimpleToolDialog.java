package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.ToolEnum;
import pers.zlf.plugin.util.StringUtil;

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
        downButton.setVisible(false);
        upButton.setVisible(false);
        if (StringUtil.isNotEmpty(toolEnum.downButtonName)) {
            downButton.setVisible(true);
            downButton.setText(toolEnum.downButtonName);
            downButton.addActionListener(e -> downTextArea.setText(toolEnum.downButtonFunction.apply(upTextArea.getText())));
        }
        if (StringUtil.isNotEmpty(toolEnum.upButtonName)) {
            upButton.setVisible(true);
            upButton.setText(toolEnum.upButtonName);
            upButton.addActionListener(e -> upTextArea.setText(toolEnum.upButtonFunction.apply(downTextArea.getText())));
        }
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
