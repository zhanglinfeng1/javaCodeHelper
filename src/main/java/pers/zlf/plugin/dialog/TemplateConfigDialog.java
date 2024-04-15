package pers.zlf.plugin.dialog;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.config.TemplateConfig;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

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

    public TemplateConfigDialog() {
        //添加
        addButton.addActionListener(e -> {
            String title = Messages.showInputDialog((Project) null, null, Common.TEMPLATE_NAME, Messages.getInformationIcon());
            if (StringUtil.isNotEmpty(title)) {
                templatePane.addTab(title, createTemplateJPanel(Common.BLANK_STRING));
            }
        });
        //删除
        deleteButton.addActionListener(e -> {
            int index = templatePane.getSelectedIndex();
            if (index != -1) {
                templatePane.remove(index);
            }
        });
        //重置
        resetButton.addActionListener(e -> {
            templatePane.removeAll();
            Map<String, String> templateMap = TemplateFactory.getInstance().getAllDefaultTemplate();
            for (Map.Entry<String, String> templateEntry : templateMap.entrySet()) {
                String key = templateEntry.getKey();
                String title = key.replace(ClassType.FREEMARKER_FILE, Common.BLANK_STRING);
                title = ClassType.JAVA_FILE.equals(title) ? Common.MODEL + ClassType.JAVA_FILE : title;
                templatePane.addTab(title, createTemplateJPanel(templateMap.get(key)));
            }
        });
    }

    public void reset() {
        TemplateConfig templateConfig = ConfigFactory.getInstance().getTemplateConfig();
        authorField.setText(templateConfig.getAuthor());
        Map<String, String> templateMap = templateConfig.getTemplateMap();
        templatePane.removeAll();
        for (Map.Entry<String, String> templateEntry : templateMap.entrySet()) {
            templatePane.addTab(templateEntry.getKey(), createTemplateJPanel(templateEntry.getValue()));
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public Map<String, String> getTemplateMap() {
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

    private JPanel createTemplateJPanel(String text) {
        JPanel jPanel = new JPanel(new GridLayout());
        JScrollPane pane = new JBScrollPane();
        LanguageTextField languageTextField = new LanguageTextField(JavaLanguage.INSTANCE, null, text, false);
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
