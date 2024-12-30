package pers.zlf.plugin.util;

import com.intellij.ide.ClipboardSynchronizer;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import kotlin.Unit;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.constant.MyIcon;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Container;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * swing 工具类
 *
 * @author zhanglinfeng
 * @date create in 2023/7/25 10:02
 */
public class SwingUtil {
    /**
     * 复制选项
     */
    public static final String MENU_ITEM_COPY = "COPY";

    /**
     * 创建右键菜单
     *
     * @param mouseEvent   鼠标事件
     * @param menuItemKeys 菜单key
     */
    public static void createJBPopupMenu(MouseEvent mouseEvent, String... menuItemKeys) {
        //添加复制菜单
        String selectedText = Common.BLANK_STRING;
        if (mouseEvent.getComponent() instanceof JTextComponent) {
            selectedText = ((JTextComponent) mouseEvent.getComponent()).getSelectedText();
        }
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            JBPopupMenu menu = new JBPopupMenu();
            for (String menuItemKey : menuItemKeys) {
                if (MENU_ITEM_COPY.equals(menuItemKey)) {
                    menu.add(createCopyMenuItem(selectedText));
                }
            }
            JBPopupMenu.showByEvent(mouseEvent, menu);
        }
    }

    /**
     * 创建复制菜单
     *
     * @param selectedText 选中的文本
     * @return JBMenuItem
     */
    public static JBMenuItem createCopyMenuItem(String selectedText) {
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_COPY);
        AbstractAction abstractAction = new AbstractAction(action.getTemplatePresentation().getText(), action.getTemplatePresentation().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection content = new StringSelection(selectedText);
                ClipboardSynchronizer.getInstance().setContent(content, content);
            }

            @Override
            public boolean isEnabled() {
                return StringUtil.isNotEmpty(selectedText);
            }
        };
        return new JBMenuItem(abstractAction);
    }

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public static void addMouseListener(JButton button, IconEnum iconEnum) {
        if (iconEnum != null) {
            button.setIcon(ColorUtil.isDark(button.getParent().getBackground()) ? iconEnum.getDarkIcon() : iconEnum.getBrightIcon());
        }
        if (button.getMouseListeners().length <= 1) {
            addMouseListener(button);
        }
        button.setEnabled(true);
    }

    /**
     * 移除按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public static void removeMouseListener(JButton button, IconEnum iconEnum) {
        Container container = button.getParent();
        if (iconEnum != null) {
            button.setIcon(ColorUtil.isDark(container.getBackground()) ? iconEnum.getBrightIcon() : iconEnum.getDarkIcon());
        }
        button.setBackground(container.getBackground());
        Arrays.stream(button.getMouseListeners()).filter(m -> !(m instanceof BasicButtonListener)).findAny().ifPresent(button::removeMouseListener);
        button.setEnabled(false);
    }

    /**
     * JTextField 鼠标聚焦失焦监听
     *
     * @param textField   JTextField
     * @param defaultText 默认文本
     */
    public static void addFocusListener(JTextField textField, String defaultText) {
        textField.setForeground(JBColor.GRAY);
        textField.setText(defaultText);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (defaultText.equals(textField.getText())) {
                    textField.setText(Common.BLANK_STRING);
                    textField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(textField.getText())) {
                    textField.setForeground(JBColor.GRAY);
                    textField.setText(defaultText);
                }
            }
        });
    }

    /**
     * 获取表格内容
     *
     * @param tableModel DefaultTableModel
     * @param columnNum  列序号
     * @return List<String>
     */
    public static List<String> getTableContentList(DefaultTableModel tableModel, int columnNum) {
        List<String> contentList = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Empty.of(tableModel.getValueAt(i, columnNum)).map(StringUtil::toString).ifPresent(contentList::add);
        }
        return contentList;
    }

    /**
     * 初始化按钮背景色
     *
     * @param buttons 按钮
     */
    public static void initButtonBackground(JButton... buttons) {
        Color color = buttons[0].getParent().getBackground();
        Arrays.stream(buttons).forEach(t -> t.setBackground(color));
    }

    /**
     * 添加按钮监听，聚焦、失焦时变色
     *
     * @param button 按钮
     */
    public static void addMouseListener(JButton button) {
        Container container = button.getParent();
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Color themeColor = container.getBackground();
                button.setBackground(ColorUtil.isDark(themeColor) ? themeColor.brighter() : themeColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(container.getBackground());
            }
        });
    }

    /**
     * 注册 ToolWindow 组件
     *
     * @param project     项目
     * @param id          id
     * @param panel       组件
     * @param displayName 组件名
     */
    public static void registerToolWindow(Project project, String id, JPanel panel, String displayName) {
        Supplier<ToolWindow> supplier = () -> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(id);
            if (toolWindow == null) {
                toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(id, builder -> {
                    builder.icon = MyIcon.LOGO;
                    builder.canCloseContent = true;
                    return Unit.INSTANCE;
                });
            }
            return toolWindow;
        };
        showToolWindow(supplier, project, panel, displayName);
    }

    /**
     * 显示 ToolWindow 组件
     *
     * @param project     项目
     * @param id          id
     * @param panel       组件
     * @param displayName 组件名
     */
    public static void showToolWindow(Project project, String id, JPanel panel, String displayName) {
        Supplier<ToolWindow> supplier = () -> ToolWindowManager.getInstance(project).getToolWindow(id);
        showToolWindow(supplier, project, panel, displayName);
    }

    /**
     * 显示 ToolWindow 组件
     *
     * @param project 项目
     * @param id      id
     */
    public static void closeToolWindowSelectedContent(Project project, String id) {
        ToolWindowManager.getInstance(project).invokeLater(() -> {
            ContentManager contentManager = ToolWindowManager.getInstance(project).getToolWindow(id).getContentManager();
            contentManager.removeContent(contentManager.getSelectedContent(), true);
        });
    }

    /**
     * 显示 ToolWindow 组件
     *
     * @param toolWindow  ToolWindow
     * @param project     项目
     * @param panel       组件
     * @param displayName 组件名
     */
    private static void showToolWindow(Supplier<ToolWindow> supplier, Project project, JPanel panel, String displayName) {
        ToolWindowManager.getInstance(project).invokeLater(() -> {
            ToolWindow toolWindow = supplier.get();
            ContentManager contentManager = toolWindow.getContentManager();
            Optional.ofNullable(contentManager.findContent(displayName)).ifPresent(t -> contentManager.removeContent(t, true));
            Content content = contentManager.getFactory().createContent(panel, displayName, false);
            content.setCloseable(true);
            contentManager.addContent(content);
            contentManager.setSelectedContent(content);
            toolWindow.show();
        });
    }
}
