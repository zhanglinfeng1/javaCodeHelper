package pers.zlf.plugin.dialog;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Equals;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 14:03
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
    private JBTable fileTypeTable;
    private JButton deleteFileTypeButton;
    private JButton addFileTypeButton;
    private JCheckBox commentCheckBox;

    public CommonConfigDialog() {
        addFocusListener(customTemplatesPathField, COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER);
        downloadButton.addActionListener(e -> {
            try {
                TemplateFactory.getInstance().download();
                Messages.showMessageDialog(COMMON.SUCCESS, COMMON.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
            }
        });
        defaultTableModel = new DefaultTableModel(null, new String[]{COMMON.BLANK_STRING});
        fileTypeTable.setModel(defaultTableModel);
        fileTypeTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addFileTypeButton.addActionListener(e -> {
            defaultTableModel.addRow(new String[]{COMMON.BLANK_STRING});
            fileTypeTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
            addMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
        });
        deleteFileTypeButton.addActionListener(e -> Equals.of(fileTypeTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            defaultTableModel.removeRow(rowNum);
            if (fileTypeTable.getRowCount() == 0) {
                removeMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
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
        Optional.ofNullable(commonConfig.getTranslateApi()).ifPresent(translateApiComboBox::setSelectedIndex);
        Optional.ofNullable(commonConfig.getDateClassType()).ifPresent(dateClassComboBox::setSelectedIndex);
        Optional.ofNullable(commonConfig.getApiTool()).ifPresent(apiToolComboBox::setSelectedIndex);
        List<String> fileTypeList = commonConfig.getFileTypeList();
        addMouseListener(addFileTypeButton, ICON_ENUM.ADD);
        if (CollectionUtil.isEmpty(fileTypeList)) {
            removeMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
        } else {
            fileTypeList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
            addMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
        }
        commentCheckBox.setSelected(commonConfig.isCountComment());
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
        return COMMON.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(path) ? COMMON.BLANK_STRING : path;
    }

    public Integer getDateClassType() {
        return dateClassComboBox.getSelectedIndex();
    }

    public Integer getApiTool() {
        return apiToolComboBox.getSelectedIndex();
    }

    public boolean isCountComment(){
        return commentCheckBox.isSelected();
    }
}