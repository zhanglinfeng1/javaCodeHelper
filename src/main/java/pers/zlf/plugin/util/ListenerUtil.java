package pers.zlf.plugin.util;

import com.intellij.ui.ColorUtil;
import pers.zlf.plugin.constant.ICON_ENUM;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/15 15:24
 */
public class ListenerUtil {

    /**
     * 鼠标监听，JPanel变色
     *
     * @param container Container
     * @return MouseListener
     */
    public static MouseListener getMouseListener(Container container) {
        return new MouseListener() {
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
                Color themeColor = container.getParent().getBackground();
                container.setBackground(ColorUtil.isDark(themeColor) ? themeColor.brighter() : themeColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                container.setBackground(container.getParent().getBackground());
            }
        };
    }

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public static void addMouseListener(JButton button, ICON_ENUM iconEnum) {
        Container container = button.getParent();
        button.setIcon(ColorUtil.isDark(container.getBackground()) ? iconEnum.getDarkIcon() : iconEnum.getBrightIcon());
        if (button.getMouseListeners().length <= 1) {
            button.addMouseListener(getMouseListener(button.getParent()));
        }
    }

    /**
     * 移除按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public static void removeMouseListener(JButton button, ICON_ENUM iconEnum) {
        Container container = button.getParent();
        button.setIcon(ColorUtil.isDark(container.getBackground()) ? iconEnum.getBrightIcon() : iconEnum.getDarkIcon());
        button.getParent().setBackground(container.getParent().getBackground());
        Arrays.stream(button.getMouseListeners()).filter(m -> !(m instanceof BasicButtonListener)).findAny().ifPresent(button::removeMouseListener);
    }
}
