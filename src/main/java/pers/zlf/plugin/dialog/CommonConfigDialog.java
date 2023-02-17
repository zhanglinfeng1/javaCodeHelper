package pers.zlf.plugin.dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import freemarker.template.Template;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 14:03
 */
public class CommonConfigDialog extends BaseDialog{
    private JPanel panel;
    private JTextField appIdTextField;
    private JTextField securityKeyTextField;
    private JComboBox<String> apiComboBox;
    private JTextField customTemplatesPathField;
    private JButton downloadButton;
    private JComboBox<String> dateClassComboBox;

    public CommonConfigDialog() {
        addFocusListener(customTemplatesPathField,COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        downloadButton.addActionListener(e -> Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null))
                .ifPresent(virtualFile -> {
                    try {
                        List<Template> defaultTemplateList = TemplateFactory.getInstance().getDefaultTemplateList();
                        for (Template template : defaultTemplateList) {
                            FileWriter file = new FileWriter(virtualFile.getPath() + COMMON.DOUBLE_BACKSLASH + template.getName(), true);
                            //TODO 寻找替换方法
                            file.append(template.getRootTreeNode().toString());
                            file.flush();
                            file.close();
                        }
                    } catch (Exception ex) {
                        Messages.showMessageDialog(ex.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
                    }
                }));
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(commonConfig.getAppId());
        securityKeyTextField.setText(commonConfig.getSecretKey());
        String customTemplatesPath = commonConfig.getCustomTemplatesPath();
        if (StringUtil.isEmpty(customTemplatesPath)) {
            customTemplatesPathField.setForeground(JBColor.GRAY);
            customTemplatesPathField.setText(COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        } else {
            customTemplatesPathField.setText(customTemplatesPath);
        }
        Optional.ofNullable(commonConfig.getApiType()).ifPresent(t -> apiComboBox.setSelectedIndex(t));
        Optional.ofNullable(commonConfig.getDateClassType()).ifPresent(t -> dateClassComboBox.setSelectedIndex(t));
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

    public Integer getApiType() {
        return apiComboBox.getSelectedIndex();
    }

    public String getCustomTemplatesPath() {
        String path = customTemplatesPathField.getText();
        return COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(path) ? COMMON.BLANK_STRING : path;
    }

    public Integer getDateClassType() {
        return dateClassComboBox.getSelectedIndex();
    }

}