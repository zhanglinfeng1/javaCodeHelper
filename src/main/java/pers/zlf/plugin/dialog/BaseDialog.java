package pers.zlf.plugin.dialog;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.util.StringUtil;

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
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/15 15:24
 */
public class BaseDialog {
    protected DefaultTableModel defaultTableModel;

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    protected void addMouseListener(JButton button, ICON_ENUM iconEnum) {
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
    protected void removeMouseListener(JButton button, ICON_ENUM iconEnum) {
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
    protected void addFocusListener(JTextField textField, String defaultText) {
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

    /**
     * 获取表格内容
     *
     * @return List<String>
     */
    public List<String> getTableContentList(Integer... columnNumArr) {
        List<String> contentList = new ArrayList<>();
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            final int rowNum = i;
            contentList.add(Arrays.stream(columnNumArr).map(columnNum -> defaultTableModel.getValueAt(rowNum, columnNum))
                    .map(StringUtil::toString).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.joining(COMMON.SEMICOLON)));
        }
        return contentList;
    }

    /**
     * 清空表格内容
     */
    public void clearTableContent() {
        defaultTableModel.getDataVector().clear();
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
