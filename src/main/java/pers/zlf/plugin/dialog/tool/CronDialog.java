package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.CronUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2024/6/18 15:56
 */
public class CronDialog {
    private JPanel contentPanel;
    private JTextArea upTextArea;
    private JButton downButton;
    private JTextArea downTextArea;

    public CronDialog() {
        downButton.addActionListener(e -> {
            String cronStr = upTextArea.getText();
            if (StringUtil.isEmpty(cronStr)) {
                return;
            }
            String downText = Message.NOT_CRON_EXPRESSIONS;
            if (CronUtil.isCron(cronStr)) {
                downText = CronUtil.getExecutionTimeList(cronStr, 5).stream().collect(Collectors.joining(System.getProperty("line.separator")));
            }
            downTextArea.setText(downText);
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
