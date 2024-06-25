package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author zhanglinfeng
 * @date create in 2024/6/25 11:16
 */
public class TimeStampDialog {
    private JTextArea upTextArea;
    private JButton downButton;
    private JTextArea downTextArea;
    private JPanel contentPanel;

    public TimeStampDialog() {
        downButton.addActionListener(e -> {
            String text = upTextArea.getText();
            if (StringUtil.isEmpty(text)) {
                return;
            }
            try {
                long timestamp = Long.parseLong(text);
                if (text.length() == 10) {
                    downTextArea.setText(DateUtil.secondsToString(timestamp, DateUtil.YYYY_MM_DDHHMMSS));
                } else if (text.length() == 13) {
                    downTextArea.setText(DateUtil.millisecondsToString(timestamp, DateUtil.YYYY_MM_DDHHMMSS_SSS));
                }
            } catch (Exception ignored) {
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
