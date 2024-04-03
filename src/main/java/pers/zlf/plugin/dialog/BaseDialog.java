package pers.zlf.plugin.dialog;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/15 15:24
 */
public interface BaseDialog {

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    default void addMouseListener(JButton button, IconEnum iconEnum) {
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
    default void removeMouseListener(JButton button, IconEnum iconEnum) {
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
    default void addFocusListener(JTextField textField, String defaultText) {
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
    default List<String> getTableContentList(DefaultTableModel tableModel, int columnNum) {
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
    default void initButtonBackground(JButton... buttons) {
        Color color = buttons[0].getParent().getBackground();
        Arrays.stream(buttons).forEach(t -> t.setBackground(color));
    }

    /**
     * 添加按钮监听，聚焦、失焦时变色
     *
     * @param button 按钮
     */
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
