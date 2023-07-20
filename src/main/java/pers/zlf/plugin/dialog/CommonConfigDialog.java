package pers.zlf.plugin.dialog;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/4 14:03
 */
public class CommonConfigDialog extends BaseDialog {
    private JPanel panel;
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> translateApiComboBox;
    private JTextField customTemplatesPathField;
    private JButton downloadButton;
    private JComboBox<String> dateClassComboBox;
    private JComboBox<String> apiToolComboBox;
    private JRadioButton codeCompletionEnableButton;
    private JRadioButton codeCompletionDisabledButton;

    public CommonConfigDialog() {
        addFocusListener(customTemplatesPathField, Common.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        downloadButton.addActionListener(e -> {
            try {
                TemplateFactory.getInstance().download();
                Messages.showMessageDialog(Common.SUCCESS, Common.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(commonConfig.getAppId());
        securityKeyTextField.setText(commonConfig.getSecretKey());
        String customTemplatesPath = commonConfig.getCustomTemplatesPath();
        if (StringUtil.isEmpty(customTemplatesPath)) {
            customTemplatesPathField.setForeground(JBColor.GRAY);
            customTemplatesPathField.setText(Common.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        } else {
            customTemplatesPathField.setText(customTemplatesPath);
        }
        Optional.ofNullable(commonConfig.getTranslateApi()).ifPresent(translateApiComboBox::setSelectedIndex);
        Optional.ofNullable(commonConfig.getDateClassType()).ifPresent(dateClassComboBox::setSelectedIndex);
        Optional.ofNullable(commonConfig.getApiTool()).ifPresent(apiToolComboBox::setSelectedIndex);
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

    public Integer getTranslateApi() {
        return translateApiComboBox.getSelectedIndex();
    }

    public String getCustomTemplatesPath() {
        String path = customTemplatesPathField.getText();
        return Common.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(path) ? Common.BLANK_STRING : path;
    }

    public Integer getDateClassType() {
        return dateClassComboBox.getSelectedIndex();
    }

    public Integer getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isEnableCodeCompletion(){
        return codeCompletionEnableButton.isSelected();
    }
}