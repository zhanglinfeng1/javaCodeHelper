package pers.zlf.plugin.dialog;

import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

import javax.swing.JCheckBox;
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
public class CommonConfigDialog extends BaseDialog{
    /** ui组件 */
    private JPanel panel;
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> translateApiComboBox;
    private JComboBox<String> apiToolComboBox;
    private JRadioButton codeCompletionEnableButton;
    private JRadioButton codeCompletionDisabledButton;
    private JTextField maxCodeCompletionLengthTextField;
    private JCheckBox braceCheckBox;
    private JCheckBox parenthCheckBox;
    private JCheckBox bracketCheckBox;
    private JCheckBox angleBracketCheckBox;

    public CommonConfigDialog() {
    }

    @Override
    public void reset() {
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(config.getAppId());
        securityKeyTextField.setText(config.getSecretKey());
        maxCodeCompletionLengthTextField.setText(String.valueOf(config.getMaxCodeCompletionLength()));

        Optional.ofNullable(config.getTranslateApi()).ifPresent(translateApiComboBox::setSelectedIndex);
        Optional.ofNullable(config.getApiTool()).ifPresent(apiToolComboBox::setSelectedIndex);

        braceCheckBox.setSelected(config.isOpenBrace());
        parenthCheckBox.setSelected(config.isOpenParenth());
        bracketCheckBox.setSelected(config.isOpenBracket());
        angleBracketCheckBox.setSelected(config.isOpenAngleBracket());
    }

    @Override
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

    public Integer getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isEnableCodeCompletion() {
        return codeCompletionEnableButton.isSelected();
    }

    public Integer getMaxCodeCompletionLength() {
        return Integer.parseInt(maxCodeCompletionLengthTextField.getText());
    }

    public boolean isOpenAngleBracket() {
        return angleBracketCheckBox.isSelected();
    }

    public boolean isOpenParenth() {
        return parenthCheckBox.isSelected();
    }

    public boolean isOpenBracket() {
        return bracketCheckBox.isSelected();
    }

    public boolean isOpenBrace() {
        return braceCheckBox.isSelected();
    }
}