package pers.zlf.plugin.dialog;

import pers.zlf.plugin.dialog.tool.AsciiDialog;
import pers.zlf.plugin.dialog.tool.BinaryConversionDialog;
import pers.zlf.plugin.dialog.tool.CronDialog;
import pers.zlf.plugin.dialog.tool.EncryptionAndDecryptionDialog;
import pers.zlf.plugin.dialog.tool.EscapeDialog;
import pers.zlf.plugin.dialog.tool.QrCodeDialog;
import pers.zlf.plugin.dialog.tool.TimeStampDialog;
import pers.zlf.plugin.dialog.tool.UnicodeDialog;
import pers.zlf.plugin.dialog.tool.UrlEncodeDialog;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/20 10:33
 */
public class CommonToolsDialog {
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;

    public CommonToolsDialog() {
        tabbedPane.add("Unicode", new UnicodeDialog().getContent());
        tabbedPane.add("UrlEncode", new UrlEncodeDialog().getContent());
        tabbedPane.add("Escape", new EscapeDialog().getContent());
        tabbedPane.add("Ascii码", new AsciiDialog().getContent());
        tabbedPane.add("二维码", new QrCodeDialog().getContent());
        tabbedPane.add("Cron表达式", new CronDialog().getContent());
        tabbedPane.add("时间戳转换", new TimeStampDialog().getContent());
        tabbedPane.add("加解密", new EncryptionAndDecryptionDialog().getContent());
        tabbedPane.add("进制转换", new BinaryConversionDialog().getContent());
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
