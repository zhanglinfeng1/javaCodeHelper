package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.util.AESUtil;

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
    private JTextField ivTextField;
    private JLabel ivLabel;

    public EncryptionAndDecryptionDialog() {
        downButton.addActionListener(e -> downTextArea.setText(encryption(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(decryption(downTextArea.getText())));
        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = e.getItem().toString();
                switch (selectedItem) {
                    case Common.BASE64:
                        initButton(true, false, false, false, false);
                        break;
                    case Common.MD5, Common.SHA1, Common.SHA256, Common.SHA512:
                        initButton(false, false, false, false, false);
                        break;
                    case Common.AES_ECB:
                        initButton(true, true, true, false, false);
                        break;
                    case Common.AES_CBC:
                        initButton(true, true, true, true, true);
                        break;
                }
            }
        });
        initButton(true, false, false, false, false);
    }

    private void initButton(boolean upButtonVisible, boolean secretKeyLabelVisible, boolean secretKeyTextFieldVisible, boolean ivLabelVisible, boolean ivTextFieldVisible) {
        downTextArea.setText(Common.BLANK_STRING);
        upButton.setVisible(upButtonVisible);
        secretKeyLabel.setVisible(secretKeyLabelVisible);
        secretKeyTextField.setVisible(secretKeyTextFieldVisible);
        ivLabel.setVisible(ivLabelVisible);
        ivTextField.setVisible(ivTextFieldVisible);
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

    private String encryption(String str) {
        String type = typeComboBox.getSelectedItem().toString();

        switch (type) {
            case Common.BASE64:
                return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
            case Common.MD5:
                return DigestUtils.md5Hex(str);
            case Common.SHA1:
                return DigestUtils.sha1Hex(str);
            case Common.SHA256:
                return DigestUtils.sha256Hex(str);
            case  Common.SHA512:
                return DigestUtils.sha512Hex(str);
            case Common.AES_ECB:
                try {
                    return AESUtil.encrypt(str, secretKeyTextField.getText());
                } catch (Exception e) {
                    Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Icon.LOGO);
                    return Common.BLANK_STRING;
                }
            case Common.AES_CBC:
                try {
                    return AESUtil.encrypt(str, secretKeyTextField.getText(), ivTextField.getText());
                } catch (Exception e) {
                    Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Icon.LOGO);
                    return Common.BLANK_STRING;
                }
            default:
                return Common.BLANK_STRING;
        }
    }

    private String decryption(String str) {
        switch (typeComboBox.getSelectedItem().toString()) {
            case Common.BASE64:
                return new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            case Common.AES_ECB:
                try {
                    return AESUtil.decrypt(str, secretKeyTextField.getText());
                } catch (Exception e) {
                    Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Icon.LOGO);
                    return Common.BLANK_STRING;
                }
            case Common.AES_CBC:
                try {
                    return AESUtil.decrypt(str, secretKeyTextField.getText(), ivTextField.getText());
                } catch (Exception e) {
                    Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Icon.LOGO);
                    return Common.BLANK_STRING;
                }
            default:
                return Common.BLANK_STRING;
        }
    }

}
