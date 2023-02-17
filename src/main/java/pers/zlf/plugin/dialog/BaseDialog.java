package pers.zlf.plugin.dialog;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/15 15:24
 */
public class BaseDialog {

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public void addMouseListener(JButton button, ICON_ENUM iconEnum) {
        button.setIcon(ColorUtil.isDark(button.getParent().getBackground()) ? iconEnum.getDarkIcon() : iconEnum.getBrightIcon());
        if (button.getMouseListeners().length <= 1) {
            addMouseListener(button);
        }
    }

    /**
     * 移除按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    public void removeMouseListener(JButton button, ICON_ENUM iconEnum) {
        Container container = button.getParent();
        button.setIcon(ColorUtil.isDark(container.getBackground()) ? iconEnum.getBrightIcon() : iconEnum.getDarkIcon());
        button.setBackground(container.getBackground());
        Arrays.stream(button.getMouseListeners()).filter(m -> !(m instanceof BasicButtonListener)).findAny().ifPresent(button::removeMouseListener);
    }

    /**
     * JTextField 鼠标聚焦失焦监听
     *
     * @param textField   JTextField
     * @param defaultText 默认文本
     */
    public void addFocusListener(JTextField textField, String defaultText) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (defaultText.equals(textField.getText())) {
                    textField.setText(COMMON.BLANK_STRING);
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

    private void addMouseListener(JButton button) {
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
}
