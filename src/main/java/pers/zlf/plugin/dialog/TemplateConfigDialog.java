package pers.zlf.plugin.dialog;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.TemplateConfig;
import pers.zlf.plugin.util.MapUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/8 15:02
 */
public class TemplateConfigDialog implements BaseDialog {
    private JPanel panel;
    private JTextField authorField;
    private JTabbedPane templatePane;
    private JButton addButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JComboBox<String> templateComboBox;
    private JButton addTemplateButton;
    private JButton editTemplateButton;
    private JButton deleteTemplateButton;
    private Map<String, Map<String, String>> totalTemplateMap;

    public TemplateConfigDialog() {
        //初始化按钮背景色
        initButtonBackground(addButton, deleteButton, resetButton, addTemplateButton, editTemplateButton, deleteTemplateButton);
        addMouseListener(addTemplateButton, IconEnum.ADD);
        addMouseListener(editTemplateButton, IconEnum.EDIT);
        addMouseListener(deleteTemplateButton, IconEnum.REMOVE);
        addMouseListener(addButton, IconEnum.ADD);
        addMouseListener(deleteButton, IconEnum.REMOVE);
        addMouseListener(resetButton, IconEnum.RESET);
        //添加
        addTemplateButton.addActionListener(e -> {
            String title = Messages.showInputDialog((Project) null, null, Message.TEMPLATE_NAME, null);
            if (StringUtil.isNotEmpty(title)) {
                Set<String> templateNameList = totalTemplateMap.keySet();
                if (templateNameList.contains(title)) {
                    Messages.showMessageDialog(Message.TEMPLATE_EXISTING, Common.BLANK_STRING, Icon.LOGO);
                    return;
                }
                templateComboBox.addItem(title);
                totalTemplateMap.put(title, new HashMap<>());
                addMouseListener(deleteTemplateButton, IconEnum.REMOVE);
                updateTemplateJPanel(new HashMap<>());
            }
        });
        //删除
        deleteTemplateButton.addActionListener(e -> {
            String selectedItem = templateComboBox.getSelectedItem().toString();
            int result = Messages.showYesNoDialog(String.format(Message.DELETE_TEMPLATE, selectedItem), Common.TEMPLATE_CONFIG, Icon.LOGO);
            if (Messages.YES == result) {
                templateComboBox.removeItem(selectedItem);
                totalTemplateMap.remove(selectedItem);
                if (templateComboBox.getItemCount() == 1) {
                    removeMouseListener(deleteTemplateButton, IconEnum.REMOVE);
                }
            }
        });
        //修改
        editTemplateButton.addActionListener(e -> {
            String oldName = templateComboBox.getSelectedItem().toString();
            String newName = Messages.showInputDialog((Project) null, null, Message.UPDATE_TEMPLATE_NAME, null);
            if (StringUtil.isNotEmpty(newName)) {
                Set<String> templateNameList = totalTemplateMap.keySet();
                templateNameList.remove(oldName);
                if (templateNameList.contains(newName)) {
                    Messages.showMessageDialog(Message.TEMPLATE_EXISTING, Common.BLANK_STRING, Icon.LOGO);
                    return;
                }
                templateComboBox.removeItem(oldName);
                templateComboBox.addItem(newName);
                totalTemplateMap.put(newName, totalTemplateMap.remove(oldName));
                templateComboBox.setSelectedItem(newName);
            }
        });
        //添加
        addButton.addActionListener(e -> {
            String title = Messages.showInputDialog((Project) null, null, Message.TEMPLATE_FILE_NAME, null);
            if (StringUtil.isNotEmpty(title)) {
                int index = templatePane.getSelectedIndex();
                if (index != -1) {
                    String selectedTitle = templatePane.getTitleAt(index);
                    Set<String> templateFileNameList = totalTemplateMap.get(selectedTitle).keySet();
                    if (templateFileNameList.contains(title)) {
                        Messages.showMessageDialog(Message.TEMPLATE_FILE_EXISTING, Common.BLANK_STRING, Icon.LOGO);
                        return;
                    }
                }
                templatePane.addTab(title, createTemplateJPanel(Common.BLANK_STRING));
                addMouseListener(deleteButton, IconEnum.REMOVE);
            }
        });
        //删除
        deleteButton.addActionListener(e -> {
            int index = templatePane.getSelectedIndex();
            String title = templatePane.getTitleAt(index);
            int result = Messages.showYesNoDialog(String.format(Message.DELETE_TEMPLATE_FILE, title), Common.TEMPLATE_CONFIG, Icon.LOGO);
            if (Messages.YES == result) {
                if (index != -1) {
                    templatePane.remove(index);
                }
                int total = templatePane.getTabCount();
                if (total == 0) {
                    removeMouseListener(deleteButton, IconEnum.REMOVE);
                }
            }
        });
        //重置
        resetButton.addActionListener(e -> {
            int result = Messages.showYesNoDialog(Message.RESET_TEMPLATE, Common.TEMPLATE_CONFIG, Icon.LOGO);
            if (Messages.YES == result) {
                updateTemplateJPanel(totalTemplateMap.get(Common.DEFAULT_TEMPLATE));
            }
        });
    }

    public void reset() {
        TemplateConfig templateConfig = ConfigFactory.getInstance().getTemplateConfig();
        authorField.setText(templateConfig.getAuthor());
        initTemplateComboBox();
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public String getSelectedTemplate() {
        return templateComboBox.getSelectedItem().toString();
    }

    public Map<String, Map<String, String>> getTotalTemplateMap() {
        return totalTemplateMap;
    }

    public Map<String, String> getCurrentTemplateMap() {
        Map<String, String> map = new HashMap<>();
        int count = templatePane.getTabCount();
        for (int i = 0; i < count; i++) {
            Component component = ((JPanel) templatePane.getComponentAt(i)).getComponent(0);
            JViewport viewport = ((JScrollPane) component).getViewport();
            LanguageTextField languageTextField = (LanguageTextField) viewport.getView();
            map.put(templatePane.getTitleAt(i), languageTextField.getText());
        }
        return map;
    }

    private void initTemplateComboBox() {
        TemplateConfig templateConfig = ConfigFactory.getInstance().getTemplateConfig();
        totalTemplateMap = templateConfig.getTotalTemplateMap();
        //初始化下拉
        templateComboBox.removeAllItems();
        totalTemplateMap.keySet().forEach(templateComboBox::addItem);
        String defaultItem = Empty.of(templateConfig.getSelectedTemplate()).orElse(Common.DEFAULT_TEMPLATE);
        templateComboBox.setSelectedItem(defaultItem);
        //初始化模版
        updateTemplateJPanel(totalTemplateMap.get(defaultItem));
        templateComboBox.addItemListener(e -> {
            String selectedItem = e.getItem().toString();
            if (e.getStateChange() == ItemEvent.DESELECTED && !Common.DEFAULT_TEMPLATE.equals(selectedItem)) {
                Map<String, String> oldTemplateMap = totalTemplateMap.get(selectedItem);
                Map<String, String> newTemplateMap = getCurrentTemplateMap();
                if (!MapUtil.equals(oldTemplateMap, newTemplateMap)) {
                    int result = Messages.showYesNoDialog(String.format(Message.UPDATE_TEMPLATE, selectedItem), Common.TEMPLATE_CONFIG, Icon.LOGO);
                    if (Messages.YES == result) {
                        totalTemplateMap.put(selectedItem, newTemplateMap);
                    }
                }
            }

            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (Common.DEFAULT_TEMPLATE.equals(selectedItem)) {
                    deleteTemplateButton.setVisible(false);
                    editTemplateButton.setVisible(false);
                    addButton.setVisible(false);
                    deleteButton.setVisible(false);
                    resetButton.setVisible(false);
                } else {
                    deleteTemplateButton.setVisible(true);
                    editTemplateButton.setVisible(true);
                    addButton.setVisible(true);
                    deleteButton.setVisible(true);
                    resetButton.setVisible(true);
                }
                updateTemplateJPanel(totalTemplateMap.get(selectedItem));
            }
        });
    }

    private void updateTemplateJPanel(Map<String, String> templatePaneMap) {
        templatePane.removeAll();
        if (templatePaneMap != null && !totalTemplateMap.isEmpty()) {
            for (Map.Entry<String, String> templateEntry : templatePaneMap.entrySet()) {
                templatePane.addTab(templateEntry.getKey(), createTemplateJPanel(templateEntry.getValue()));
            }
        } else {
            removeMouseListener(deleteButton, IconEnum.REMOVE);
        }
    }

    private JPanel createTemplateJPanel(String text) {
        JPanel jPanel = new JPanel(new GridLayout());
        JScrollPane pane = new JBScrollPane();
        LanguageTextField languageTextField = new LanguageTextField(JavaLanguage.INSTANCE, null, text, false);
        Color themeColor = panel.getBackground();
        languageTextField.setBackground(ColorUtil.isDark(themeColor) ? themeColor.darker() : themeColor.brighter());
        pane.setViewportView(languageTextField);
        jPanel.add(pane);
        languageTextField.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                pane.revalidate();
                pane.repaint();
            }
        });
        return jPanel;
    }
}
