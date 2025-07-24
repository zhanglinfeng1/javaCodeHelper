package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.util.HttpUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author zhanglinfeng
 * @date create in 2025/7/24 23:33
 */
public class IpDialog {
    private JTextArea textArea;
    private JPanel contentPanel;
    private JButton refreshButton;

    public IpDialog() {
        refresh();
        refreshButton.addActionListener(e -> refresh());
    }

    public void refresh() {
        String localIp = "内网IP:";
        try {
            localIp = localIp + HttpUtil.getLocalIp();
        } catch (UnknownHostException e) {
            localIp = localIp + "获取异常：" + e.getMessage();
        }
        String externalIP = "外网IP:";
        try {
            externalIP = externalIP + HttpUtil.getExternalIP();
        } catch (IOException e) {
            externalIP = externalIP + "获取异常：" + e.getMessage();
        }
        textArea.setText(localIp + System.lineSeparator() + externalIP);
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
