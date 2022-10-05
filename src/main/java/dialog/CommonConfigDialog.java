package dialog;

import com.intellij.openapi.components.ServiceManager;
import component.ConfigComponent;
import constant.COMMON_CONSTANT;
import pojo.Config;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 14:03
 */
public class CommonConfigDialog extends JDialog {
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> apiComboBox;
    private JPanel panel;
    private final Config config = ServiceManager.getService(ConfigComponent.class).getState();

    public CommonConfigDialog() {
        appIdTextField.setText(config.getAppId());
        securityKeyTextField.setText(config.getSecretKey());
    }

    public void reset() {
        appIdTextField.setText(config.getAppId());
        securityKeyTextField.setText(config.getSecretKey());
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getAppId() {
        return appIdTextField.getText();
    }

    public String getSecurityKey() {
        return securityKeyTextField.getText();
    }

    public String getApi() {
        if (null == apiComboBox.getSelectedItem()){
            return COMMON_CONSTANT.BLANK_STRING;
        }
        return apiComboBox.getSelectedItem().toString();
    }
}
