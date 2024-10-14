package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import pers.zlf.plugin.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/14 10:53
 */
public class ToolAction extends BaseAction {
    private final String UPPER_CASE = "大写";
    private final String LOWER_CASE = "小写";
    private final String HUMP = "转驼峰";
    private final String LOWERCASE_UNDERLINE = "转小写下划线";
    private final Map<String, Function<String, String>> functionMap = new HashMap<>() {{
        put(UPPER_CASE, String::toUpperCase);
        put(LOWER_CASE, String::toLowerCase);
        put(HUMP, StringUtil::toHumpStyle);
        put(LOWERCASE_UNDERLINE, StringUtil::toUnderlineStyle);
    }};
    /** 选中的文本的相关信息 */
    private SelectionModel selectionModel;
    /** 选中的文本 */
    private String selectionText;

    @Override
    public boolean isVisible() {
        this.selectionModel = editor.getSelectionModel();
        this.selectionText = selectionModel.getSelectedText();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        return null != editor && StringUtil.isNotEmpty(selectionText) && null != psiFile && psiFile.isWritable();
    }

    @Override
    public void execute() {
        JBPopupFactory.getInstance().createPopupChooserBuilder(List.of(UPPER_CASE, LOWER_CASE, HUMP, LOWERCASE_UNDERLINE)).setMovable(true)
                .setItemChosenCallback(value -> {
                    String result = functionMap.get(value).apply(selectionText);
                    WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), result));
                }).createPopup().showInBestPositionFor(editor);
    }
}
