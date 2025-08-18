package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.HttpUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * @author zhanglinfeng
 * @date create in 2025/7/24 23:33
 */
public class IpDialog {
    private JPanel contentPanel;
    private JButton refreshButton;
    private JButton localIpCopyButton;
    private JButton externalIPCopyButton;
    private JLabel localIpLabel;
    private JLabel externalIPLabel;

    public IpDialog() {
        refresh();
        refreshButton.addActionListener(e -> refresh());
        Consumer<String> copy = text -> {
            StringSelection stringSelection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            Message.notifyInfo("已复制", 3000);
        };
        localIpCopyButton.addActionListener(e -> copy.accept(localIpLabel.getText()));
        externalIPCopyButton.addActionListener(e -> copy.accept(externalIPLabel.getText()));
    }

    public void refresh() {
        try {
            localIpLabel.setText(HttpUtil.getLocalIp());
        } catch (UnknownHostException e) {
            localIpLabel.setText("获取异常：" + e.getMessage());
        }
        try {
            externalIPLabel.setText(HttpUtil.getExternalIP());
        } catch (IOException e) {
            externalIPLabel.setText("获取异常：" + e.getMessage());
        }
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
