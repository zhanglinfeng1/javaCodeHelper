package pers.zlf.plugin.dialog;

import com.intellij.ui.components.fields.IntegerField;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ItemEvent;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/4 14:03
 */
public class CommonConfigDialog extends BaseDialog{
    /** ui组件 */
    private JPanel panel;
    private JComboBox<String> translateApiComboBox;
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> apiToolComboBox;
    private IntegerField maxCodeCompletionLengthTextField;
    private JCheckBox braceCheckBox;
    private JCheckBox parenthCheckBox;
    private JCheckBox bracketCheckBox;
    private JCheckBox angleBracketCheckBox;
    private JCheckBox codeCompletionEnableCheckBox;
    private JCheckBox codeRemindCheckBox;
    private IntegerField codeRemindMinuteTextField;
    private JPanel codeRemindPanel;
    private JComboBox<String> ocrApiComboBox;
    private JTextField ocrAppKeyTextField;
    private JTextField ocrSecurityKeyKeyTextField;
    private JTextField zenTaoUrlTextField;
    private JTextField zenTaoAccountTextField;
    private JTextField zenTaoPasswordTextField;
    private JCheckBox zenTaoRemindCheckBox;
    private IntegerField zenTaoRemindMinuteTextField;
    private JPanel zenTaoRemindPanel;

    public CommonConfigDialog() {
    }

    @Override
    public void reset() {
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
        //翻译
        translateApiComboBox.setSelectedIndex(config.getTranslateApi());
        appIdTextField.setText(config.getAppId());
        securityKeyTextField.setText(config.getSecretKey());
        //文字识别
        ocrApiComboBox.setSelectedIndex(config.getOcrApi());
        ocrAppKeyTextField.setText(config.getOcrApiKey());
        ocrSecurityKeyKeyTextField.setText(config.getOcrSecurityKey());
        //接口文档注解
        apiToolComboBox.setSelectedIndex(config.getApiTool());
        //代码补全
        codeCompletionEnableCheckBox.setSelected(config.isEnableCodeCompletion());
        maxCodeCompletionLengthTextField.setValue(config.getMaxCodeCompletionLength());
        //彩虹括号
        braceCheckBox.setSelected(config.isOpenBrace());
        parenthCheckBox.setSelected(config.isOpenParenth());
        bracketCheckBox.setSelected(config.isOpenBracket());
        angleBracketCheckBox.setSelected(config.isOpenAngleBracket());
        //git代码提醒
        codeRemindCheckBox.setSelected(config.isOpenCodeRemind());
        if (codeRemindCheckBox.isSelected()) {
            codeRemindPanel.setVisible(true);
            codeRemindMinuteTextField.setValue(config.getCodeRemindMinute());
        } else {
            codeRemindPanel.setVisible(false);
        }
        codeRemindCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                codeRemindPanel.setVisible(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                codeRemindPanel.setVisible(false);
            }
        });
        //禅道新任务、新BUG提醒
        zenTaoRemindCheckBox.setSelected(config.isOpenZenTaoRemind());
        if (zenTaoRemindCheckBox.isSelected()) {
            zenTaoRemindPanel.setVisible(true);
            zenTaoRemindMinuteTextField.setValue(config.getZenTaoRemindMinute());
        } else {
            zenTaoRemindPanel.setVisible(false);
        }
        zenTaoRemindCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                zenTaoRemindPanel.setVisible(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                zenTaoRemindPanel.setVisible(false);
            }
        });
        //禅道配置
        zenTaoUrlTextField.setText(config.getZenTaoUrl());
        zenTaoAccountTextField.setText(config.getZenTaoAccount());
        zenTaoPasswordTextField.setText(config.getZenTaoPassword());
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
        if (isOpenCodeRemind()) {
            return codeRemindMinuteTextField.getValue();
        } else {
            return ConfigFactory.getInstance().getCommonConfig().getCodeRemindMinute();
        }
    }

    public int getOcrApi() {
        return ocrApiComboBox.getSelectedIndex();
    }

    public String getOcrApiKey() {
        return ocrAppKeyTextField.getText();
    }

    public String getOcrSecurityKey() {
        return ocrSecurityKeyKeyTextField.getText();
    }

    public String getZenTaoUrl() {
        return zenTaoUrlTextField.getText();
    }

    public String getZenTaoAccount() {
        return zenTaoAccountTextField.getText();
    }

    public String getZenTaoPassword() {
        return zenTaoPasswordTextField.getText();
    }

    public boolean isOpenZenTaoRemind() {
        return zenTaoRemindCheckBox.isSelected();
    }

    public int getZenTaoRemindMinute() {
        if (isOpenZenTaoRemind()) {
            return zenTaoRemindMinuteTextField.getValue();
        } else {
            return ConfigFactory.getInstance().getCommonConfig().getZenTaoRemindMinute();
        }
    }
}