package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.MessageDigestUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ItemEvent;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/7 16:39
 */
public class EncryptionAndDecryptionDialog {
    private JTextArea upTextArea;
    private JButton downButton;
    private JButton upButton;
    private JTextArea downTextArea;
    private JComboBox<String> typeComboBox;
    private JPanel contentPanel;
    private JTextField secretKeyTextField;
    private JLabel secretKeyLabel;

    public EncryptionAndDecryptionDialog() {
        downButton.addActionListener(e -> downTextArea.setText(encryption(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(decryption(downTextArea.getText())));
        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = e.getItem().toString();
                initButton();
                switch (selectedItem) {
                    case Common.BASE64:
                        upButton.setVisible(true);
                }
            }
        });
        secretKeyLabel.setVisible(false);
        secretKeyTextField.setVisible(false);
    }

    private void initButton() {
        downTextArea.setText(Common.BLANK_STRING);
        upButton.setVisible(false);
        secretKeyLabel.setVisible(false);
        secretKeyTextField.setVisible(false);
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

    private String encryption(String str) {
        String type = typeComboBox.getSelectedItem().toString();
        return switch (type) {
            case Common.BASE64 -> Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
            case Common.MD5, Common.SHA1, Common.SHA256, Common.SHA512 -> MessageDigestUtil.encode(type, str);
            default -> Common.BLANK_STRING;
        };
    }

    private String decryption(String str) {
        String type = typeComboBox.getSelectedItem().toString();
        return switch (type) {
            case Common.BASE64 ->  new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            default -> Common.BLANK_STRING;
        };
    }

}
