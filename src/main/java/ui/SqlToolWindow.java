package ui;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/29 13:59
 */
public class SqlToolWindow {

    private JButton formatBtn;
    private JTextPane textPanel;
    private JPanel centerPanel;

    public SqlToolWindow() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane1 = new JScrollPane();
        textPanel = new JTextPane();
        scrollPane1.setViewportView(textPanel);
        centerPanel.add(scrollPane1, BorderLayout.CENTER);
        formatBtn = new JButton();
        formatBtn.setText("格式化");
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.add(centerPanel, BorderLayout.NORTH);
        panel1.add(formatBtn, BorderLayout.SOUTH);

    }

    public JPanel getContent() {
        return this.centerPanel;
    }

}