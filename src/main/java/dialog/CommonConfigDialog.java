package dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import constant.COMMON;
import factory.ConfigFactory;
import factory.TemplateFactory;
import freemarker.template.Template;
import pojo.CommonConfig;
import util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileWriter;
import java.util.List;

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
    private JTextField controllerFolderNameTextField;
    private JTextField feignFolderNameTextField;
    private JTextField customTemplatesPathField;
    private JButton downloadButton;
    private JComboBox<String> dateClassComboBox;

    public CommonConfigDialog() {
        customTemplatesPathField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(customTemplatesPathField.getText())) {
                    customTemplatesPathField.setText(COMMON.BLANK_STRING);
                    customTemplatesPathField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(customTemplatesPathField.getText())) {
                    customTemplatesPathField.setForeground(JBColor.GRAY);
                    customTemplatesPathField.setText(COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
                }
            }
        });

        downloadButton.addActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
            if (virtualFile != null) {
                String path = virtualFile.getPath();
                try {
                    List<Template> defaultTemplateList = TemplateFactory.getInstance().getDefaultTemplateList();
                    for (Template template : defaultTemplateList) {
                        FileWriter file = new FileWriter(path + COMMON.DOUBLE_BACKSLASH + template.getName(), true);
                        //TODO 寻找替换方法
                        file.append(template.getRootTreeNode().toString());
                        file.flush();
                        file.close();
                    }
                } catch (Exception ex) {
                    Messages.showMessageDialog(ex.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
                }
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        appIdTextField.setText(commonConfig.getAppId());
        securityKeyTextField.setText(commonConfig.getSecretKey());
        controllerFolderNameTextField.setText(commonConfig.getControllerFolderName());
        feignFolderNameTextField.setText(commonConfig.getFeignFolderName());
        if (COMMON.MODULAR.equals(commonConfig.getFastJumpType())) {
            modularRadioButton.setSelected(true);
            gatewayRadioButton.setSelected(false);
        } else {
            modularRadioButton.setSelected(false);
            gatewayRadioButton.setSelected(true);
        }
        String customTemplatesPath = commonConfig.getCustomTemplatesPath();
        if (StringUtil.isEmpty(customTemplatesPath)) {
            customTemplatesPathField.setForeground(JBColor.GRAY);
            customTemplatesPathField.setText(COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        } else {
            customTemplatesPathField.setText(customTemplatesPath);
        }
        Integer api = commonConfig.getApiType();
        if (null != api) {
            apiComboBox.setSelectedIndex(api);
        }
        Integer dateClassType = commonConfig.getDateClassType();
        if (null != dateClassType) {
            dateClassComboBox.setSelectedIndex(dateClassType);
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

    public Integer getApiType() {
        return apiComboBox.getSelectedIndex();
    }

    public String getFastJumpType() {
        if (modularRadioButton.isSelected()) {
            return COMMON.MODULAR;
        }
        return COMMON.GATEWAY;
    }

    public String getControllerFolderName() {
        return controllerFolderNameTextField.getText();
    }

    public String getFeignFolderName() {
        return feignFolderNameTextField.getText();
    }

    public String getCustomTemplatesPath() {
        String path = customTemplatesPathField.getText();
        return COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(path) ? COMMON.BLANK_STRING : path;
    }

    public Integer getDateClassType() {
        return dateClassComboBox.getSelectedIndex();
    }
}