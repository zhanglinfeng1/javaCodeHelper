package pers.zlf.plugin.dialog;

import com.intellij.openapi.ui.Messages;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

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
    private JTextField maxCodeCompletionLengthTextField;

    public CommonConfigDialog() {
        downloadButton.addActionListener(e -> {
            try {
                boolean success = TemplateFactory.getInstance().download();
                if (success) {
                    Messages.showMessageDialog(Common.SUCCESS, Common.BLANK_STRING, Messages.getInformationIcon());
                }
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(commonConfig.getAppId());
        securityKeyTextField.setText(commonConfig.getSecretKey());
        maxCodeCompletionLengthTextField.setText(String.valueOf(commonConfig.getMaxCodeCompletionLength()));
        customTemplatesPathField.setText(commonConfig.getCustomTemplatesPath());

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
        return customTemplatesPathField.getText();
    }

    public Integer getDateClassType() {
        return dateClassComboBox.getSelectedIndex();
    }

    public Integer getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isEnableCodeCompletion() {
        return codeCompletionEnableButton.isSelected();
    }

    public Integer getMaxCodeCompletionLength() {
        return Integer.parseInt(maxCodeCompletionLengthTextField.getText());
    }
}