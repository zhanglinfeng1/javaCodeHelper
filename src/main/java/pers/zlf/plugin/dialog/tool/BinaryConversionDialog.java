package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.Message;

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
            int num;
            try {
                num = Integer.parseInt(upTextArea.getText());
            } catch (Exception ex) {
                Message.showMessage(Message.FORMAT_ERROR);
                return;
            }
            downTextArea.setText(Integer.toString(num, getRadix()));
        });
        upButton.addActionListener(e -> {
            try {
                upTextArea.setText(String.valueOf(Integer.parseInt(downTextArea.getText(), getRadix())));
            } catch (Exception ex) {
                Message.showMessage(Message.FORMAT_ERROR);
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

    private int getRadix() {
        int selectedIndex = typeComboBox.getSelectedIndex();
        return switch (selectedIndex) {
            case 0 -> 2;
            case 1 -> 8;
            case 2 -> 16;
            default -> 10;
        };
    }
}
