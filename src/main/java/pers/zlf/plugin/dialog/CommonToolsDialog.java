package pers.zlf.plugin.dialog;

import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.ToolEnum;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/20 10:33
 */
public class CommonToolsDialog extends BaseDialog {
    private JPanel contentPane;
    private JButton unicodeButton;
    private JButton urlEncodeButton;
    private JPanel unicodeButtonPanel;
    private JPanel urlEncodeButtonPanel;
    private JTextArea upTextArea;
    private JTextArea downTextArea;
    private JButton upButton;
    private JButton downButton;
    /** 当前选中的按钮 */
    private String currentTool;

    public CommonToolsDialog() {
        //默认选择unicode工具
        currentTool = StringUtil.toLowerCaseFirst(unicodeButton.getText());
        unicodeButtonPanel.setBackground(JBColor.BLUE);
        //初始化工具按钮
        initToolButton(this);
        //执行按钮监听
        downButton.addActionListener(e -> downTextArea.setText(ToolEnum.positive(currentTool, upTextArea.getText())));
        upButton.addActionListener(e -> upTextArea.setText(ToolEnum.negative(currentTool, downTextArea.getText())));
    }

    public JPanel getContent() {
        return this.contentPane;
    }

    private void initToolButton(Object obj) {
        List<JButton> buttonList = new ArrayList<>();
        Map<String, JPanel> paneMap = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                if (field.getType() == JButton.class) {
                    JButton button = (JButton) field.get(obj);
                    if (StringUtil.isNotEmpty(button.getText())) {
                        buttonList.add(button);
                    }
                } else if (field.getType() == JPanel.class) {
                    JPanel panel = (JPanel) field.get(obj);
                    paneMap.put(field.getName(), panel);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        buttonList.forEach(button -> button.addActionListener(e -> {
            //TODO 改变按钮颜色
            currentTool = StringUtil.toLowerCaseFirst(button.getText());
            paneMap.forEach((key, value) -> value.setBackground(key.startsWith(currentTool) ? JBColor.BLUE : value.getParent().getBackground()));
        }));
    }
}
