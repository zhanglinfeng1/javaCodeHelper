package pers.zlf.plugin.util;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonListener;
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
     * @param jPanel JPanel
     * @return MouseListener
     */
    public static MouseListener getMouseListener(JPanel jPanel) {
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
                jPanel.setBackground(Gray._80);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jPanel.setBackground(JBColor.LIGHT_GRAY);
            }
        };
    }

    /**
     * 添加按钮的鼠标监听
     *
     * @param button JButton
     * @param icon   Icon
     */
    public static void addMouseListener(JButton button, Icon icon) {
        button.setIcon(icon);
        if (button.getMouseListeners().length <= 1) {
            button.addMouseListener(getMouseListener((JPanel) button.getParent()));
        }
    }

    /**
     * 移除按钮的鼠标监听
     *
     * @param button JButton
     * @param icon   Icon
     */
    public static void removeMouseListener(JButton button, Icon icon) {
        button.setIcon(icon);
        button.getParent().setBackground(JBColor.LIGHT_GRAY);
        Arrays.stream(button.getMouseListeners()).filter(m -> !(m instanceof BasicButtonListener)).findAny().ifPresent(button::removeMouseListener);
    }
}
