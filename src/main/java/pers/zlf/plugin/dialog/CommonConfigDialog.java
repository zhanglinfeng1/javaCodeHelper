package pers.zlf.plugin.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
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
import javax.swing.table.DefaultTableModel;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/4 14:03
 */
public class CommonConfigDialog implements BaseDialog {
    private JPanel panel;
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> translateApiComboBox;
    private TextFieldWithBrowseButton customTemplatesPathField;
    private JButton downloadButton;
    private JComboBox<String> apiToolComboBox;
    private JRadioButton codeCompletionEnableButton;
    private JRadioButton codeCompletionDisabledButton;
    private JTextField maxCodeCompletionLengthTextField;
    private JTextField authorField;
    private DefaultTableModel defaultTableModel;

    public CommonConfigDialog() {
        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        customTemplatesPathField.addBrowseFolderListener(new TextBrowseFolderListener(chooserDescriptor));
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
        authorField.setText(commonConfig.getAuthor());

        Optional.ofNullable(commonConfig.getTranslateApi()).ifPresent(translateApiComboBox::setSelectedIndex);
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

    public Integer getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isEnableCodeCompletion() {
        return codeCompletionEnableButton.isSelected();
    }

    public Integer getMaxCodeCompletionLength() {
        return Integer.parseInt(maxCodeCompletionLengthTextField.getText());
    }

    public String getAuthor() {
        return authorField.getText();
    }
}