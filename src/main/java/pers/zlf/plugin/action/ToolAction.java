package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/14 10:53
 */
public class ToolAction extends BaseAction {
    private final String UPPER_CASE = "大写";
    private final String LOWER_CASE = "小写";
    private final String HUMP = "下划线转驼峰";

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
        JBPopupFactory.getInstance().createPopupChooserBuilder(List.of(UPPER_CASE, LOWER_CASE, HUMP)).setTitle(Common.TOOL).setMovable(true)
                .setItemChosenCallback(value -> {
                    String result;
                    switch (value) {
                        case UPPER_CASE:
                            result = selectionText.toUpperCase();
                            break;
                        case LOWER_CASE:
                            result = selectionText.toLowerCase();
                            break;
                        case HUMP:
                            result = StringUtil.toHumpStyle(selectionText);
                            break;
                        default:
                            return;
                    }
                    WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), result));
                }).createPopup().showInBestPositionFor(editor);
    }
}
