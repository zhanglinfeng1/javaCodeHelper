package pers.zlf.plugin.dialog.tool;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/10/23 15:48
 */
public class AsciiDialog {
    private JPanel contentPanel;
    private JTextArea upTextArea;
    private JButton downButton;
    private JButton upButton;
    private JTextArea downTextArea;

    public AsciiDialog() {
        downButton.addActionListener(e -> downTextArea.setText(toAscii(upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(asciiToString(downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

    private String toAscii(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            result.append((int) c).append(Common.SPACE);
        }
        return result.toString();
    }

    private String asciiToString(String str) {
        return Arrays.stream(str.split(Common.SPACE)).filter(StringUtil::isNotEmpty).map(t -> {
            try {
                char c = (char) Integer.parseInt(t);
                return Character.toString(c);
            } catch (Exception e) {
                return t;
            }
        }).collect(Collectors.joining());
    }

}
