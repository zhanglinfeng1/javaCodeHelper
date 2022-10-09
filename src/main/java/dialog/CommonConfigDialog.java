package dialog;

import constant.COMMON_CONSTANT;
import factory.ConfigFactory;
import pojo.CommonConfig;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 14:03
 */
public class CommonConfigDialog {
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> apiComboBox;
    private JPanel panel;
    private JRadioButton modularRadioButton;
    private JRadioButton gatewayRadioButton;

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(commonConfig.getAppId());
        securityKeyTextField.setText(commonConfig.getSecretKey());
        if (COMMON_CONSTANT.MODULAR.equals(commonConfig.getFeignFastJumpType())) {
            modularRadioButton.setSelected(true);
            gatewayRadioButton.setSelected(false);
        } else {
            modularRadioButton.setSelected(false);
            gatewayRadioButton.setSelected(true);
        }
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
        if (null == apiComboBox.getSelectedItem()) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        return apiComboBox.getSelectedItem().toString();
    }

    public String getFeignFastJumpType() {
        if (modularRadioButton.isSelected()) {
            return COMMON_CONSTANT.MODULAR;
        }
        return COMMON_CONSTANT.GATEWAY;
    }

}
