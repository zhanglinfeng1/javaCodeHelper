package pers.zlf.plugin.util;

import com.intellij.ide.ClipboardSynchronizer;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import pers.zlf.plugin.constant.Common;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * swing 工具类
 *
 * @author zhanglinfeng
 * @date create in 2023/7/25 10:02
 */
public class SwingUtil {
    /** 复制选项 */
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
            // TODO 兼容      2020.2.4及以下不支持JBPopupMenu.showByEvent(mouseEvent, menu);
            menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
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
}
