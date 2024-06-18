package pers.zlf.plugin.dialog.tool;

import com.cronutils.model.CronType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.CronUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.HashMap;
import java.util.Map;
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
    private JComboBox<String> cronTypeComboBox;
    Map<String, CronType> cronTypeMap = new HashMap<>() {{
        put("CRON4J", CronType.CRON4J);
        put("QUARTZ", CronType.QUARTZ);
        put("UNIX", CronType.UNIX);
        put("SPRING", CronType.SPRING);
        put("SPRING53", CronType.SPRING53);
    }};

    public CronDialog() {
        downButton.addActionListener(e -> {
            String cronStr = upTextArea.getText();
            if (StringUtil.isEmpty(cronStr)) {
                return;
            }
            String downText = Message.NOT_CRON_EXPRESSIONS;
            CronType cronType = cronTypeMap.get(cronTypeComboBox.getSelectedItem().toString());
            if (CronUtil.isCron(cronStr, cronType)) {
                downText = CronUtil.getExecutionTimeList(cronStr, cronType, 5).stream().collect(Collectors.joining(System.lineSeparator()));
            }
            downTextArea.setText(downText);
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
