package pers.zlf.plugin.dialog;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class BaseDialog extends JDialog {
    protected DefaultTableModel defaultTableModel;

    /**
     * 添加按钮的鼠标监听
     *
     * @param button   JButton
     * @param iconEnum ICON_ENUM
     */
    protected void addMouseListener(JButton button, IconEnum iconEnum) {
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
    protected void removeMouseListener(JButton button, IconEnum iconEnum) {
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
        addFocusListener(textField, defaultText, false);
    }

    /**
     * JTextField 鼠标聚焦失焦监听
     *
     * @param textField           JTextField
     * @param defaultText         默认文本
     * @param addDocumentListener true:添加文本变化监听 false:不添加
     */
    protected void addFocusListener(JTextField textField, String defaultText, boolean addDocumentListener) {
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
        if (addDocumentListener) {
            Runnable runnable = () -> {
                if (defaultText.equals(textField.getText())) {
                    textField.setForeground(JBColor.GRAY);
                } else {
                    textField.setForeground(JBColor.BLACK);
                }
            };
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    runnable.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    runnable.run();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    runnable.run();
                }
            });
        }
    }

    /**
     * 获取表格内容
     *
     * @param tableModel DefaultTableModel
     * @param columnNum         列序号
     * @return List<String>
     */
    protected List<String> getTableContentList(DefaultTableModel tableModel, int columnNum) {
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
    protected void initButtonBackground(JButton... buttons) {
        Color color = buttons[0].getParent().getBackground();
        Arrays.stream(buttons).forEach(t -> t.setBackground(color));
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

    /**
     * 展示
     */
    public void open(){
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
