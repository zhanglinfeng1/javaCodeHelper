package pers.zlf.plugin.dialog;

import pers.zlf.plugin.dialog.tool.AsciiDialog;
import pers.zlf.plugin.dialog.tool.QrCodeDialog;
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
        //TODO 更改选中标签的颜色
        tabbedPane.add("Unicode", new UnicodeDialog().getContent());
        tabbedPane.add("UrlEncode", new UrlEncodeDialog().getContent());
        tabbedPane.add("Ascii", new AsciiDialog().getContent());
        tabbedPane.add("二维码", new QrCodeDialog().getContent());
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
