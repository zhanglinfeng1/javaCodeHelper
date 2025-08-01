package pers.zlf.plugin.dialog;

import pers.zlf.plugin.constant.ToolEnum;
import pers.zlf.plugin.dialog.tool.BinaryConversionDialog;
import pers.zlf.plugin.dialog.tool.CronDialog;
import pers.zlf.plugin.dialog.tool.EncryptionAndDecryptionDialog;
import pers.zlf.plugin.dialog.tool.IpDialog;
import pers.zlf.plugin.dialog.tool.OcrDialog;
import pers.zlf.plugin.dialog.tool.PicToBase64Dialog;
import pers.zlf.plugin.dialog.tool.PicToPdfDialog;
import pers.zlf.plugin.dialog.tool.QrCodeDialog;
import pers.zlf.plugin.dialog.tool.SimpleToolDialog;

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
        tabbedPane.add("Unicode", new SimpleToolDialog(ToolEnum.UNICODE).getContent());
        tabbedPane.add("UrlEncode", new SimpleToolDialog(ToolEnum.URL_ENCODE).getContent());
        tabbedPane.add("文字识别", new OcrDialog().getContent());
        tabbedPane.add("Ascii码", new SimpleToolDialog(ToolEnum.ASCII).getContent());
        tabbedPane.add("二维码", new QrCodeDialog().getContent());
        tabbedPane.add("Cron表达式", new CronDialog().getContent());
        tabbedPane.add("时间戳转换", new SimpleToolDialog(ToolEnum.TIMESTAMP_CONVERSION).getContent());
        tabbedPane.add("加解密", new EncryptionAndDecryptionDialog().getContent());
        tabbedPane.add("进制转换", new BinaryConversionDialog().getContent());
        tabbedPane.add("Escape", new SimpleToolDialog(ToolEnum.ESCAPE).getContent());
        tabbedPane.add("图片转Base64", new PicToBase64Dialog().getContent());
        tabbedPane.add("图片转pdf", new PicToPdfDialog().getContent());
        tabbedPane.add("本机IP", new IpDialog().getContent());
        tabbedPane.add("去除html格式", new SimpleToolDialog(ToolEnum.REMOVE_HTML_FORMAT).getContent());
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
