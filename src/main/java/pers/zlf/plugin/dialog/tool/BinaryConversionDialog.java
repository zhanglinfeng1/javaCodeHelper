package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.ui.Messages;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.MyIcon;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2024/10/31 9:28
 */
public class BinaryConversionDialog {
    private JTextArea upTextArea;
    private JButton downButton;
    private JComboBox<String> typeComboBox;
    private JTextArea downTextArea;
    private JButton upButton;
    private JPanel contentPanel;

    public BinaryConversionDialog() {
        downButton.addActionListener(e -> {
            int num = Integer.parseInt(upTextArea.getText());
            int selectedIndex = typeComboBox.getSelectedIndex();
            int radix = switch (selectedIndex) {
                case 0 -> 2;
                case 1 -> 8;
                case 2 -> 16;
                default -> 10;
            };
            try {
                downTextArea.setText(Integer.toString(num, radix));
            } catch (Exception ex) {
                Messages.showMessageDialog(Message.FORMAT_ERROR, Common.BLANK_STRING, MyIcon.LOGO);
            }
        });
        upButton.addActionListener(e -> {
            String value = downTextArea.getText();
            int selectedIndex = typeComboBox.getSelectedIndex();
            int radix = switch (selectedIndex) {
                case 0 -> 2;
                case 1 -> 8;
                case 2 -> 16;
                default -> 10;
            };
            try {
                upTextArea.setText(String.valueOf(Integer.parseInt(value, radix)));
            } catch (Exception ex) {
                Messages.showMessageDialog(Message.FORMAT_ERROR, Common.BLANK_STRING, MyIcon.LOGO);
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
