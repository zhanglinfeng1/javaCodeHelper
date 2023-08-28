package pers.zlf.plugin.dialog;

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
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
