package pers.zlf.plugin.dialog;

import com.intellij.ui.components.fields.IntegerField;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
    private IntegerField maxCodeCompletionLengthTextField;
    private JCheckBox braceCheckBox;
    private JCheckBox parenthCheckBox;
    private JCheckBox bracketCheckBox;
    private JCheckBox angleBracketCheckBox;
    private JCheckBox codeCompletionEnableCheckBox;
    private JCheckBox codeRemindCheckBox;
    private IntegerField codeRemindMinuteTextField;

    public CommonConfigDialog() {
    }

    @Override
    public void reset() {
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(config.getAppId());
        securityKeyTextField.setText(config.getSecretKey());
        maxCodeCompletionLengthTextField.setValue(config.getMaxCodeCompletionLength());
        codeRemindMinuteTextField.setValue(config.getCodeRemindMinute());

        translateApiComboBox.setSelectedIndex(config.getTranslateApi());
        apiToolComboBox.setSelectedIndex(config.getApiTool());

        braceCheckBox.setSelected(config.isOpenBrace());
        parenthCheckBox.setSelected(config.isOpenParenth());
        bracketCheckBox.setSelected(config.isOpenBracket());
        angleBracketCheckBox.setSelected(config.isOpenAngleBracket());
        codeCompletionEnableCheckBox.setSelected(config.isEnableCodeCompletion());
        codeRemindCheckBox.setSelected(config.isOpenCodeRemind());
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

    public int getTranslateApi() {
        return translateApiComboBox.getSelectedIndex();
    }

    public int getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isEnableCodeCompletion() {
        return codeCompletionEnableCheckBox.isSelected();
    }

    public int getMaxCodeCompletionLength() {
        return maxCodeCompletionLengthTextField.getValue();
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

    public boolean isOpenCodeRemind() {
        return codeRemindCheckBox.isSelected();
    }

    public int getCodeRemindMinute() {
        return codeRemindMinuteTextField.getValue();
    }
}