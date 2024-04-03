package pers.zlf.plugin.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.GenerateCodeDialog;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/19 18:13
 */
public class GenerateCodeAction extends BaseAction {
    /** 选中的表 */
    private DbTable selectDbTable;

    @Override
    public boolean isVisible() {
        //获取选中的PSI元素
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        if (psiElement instanceof DbTable) {
            selectDbTable = (DbTable) psiElement;
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Common.JAVA_CODE_HELPER);
        ContentManager contentManager = toolWindow.getContentManager();
        Optional.ofNullable(contentManager.getContent(1)).ifPresent(t -> contentManager.removeContent(t, true));
        //生成代码的窗口
        GenerateCodeDialog generateCodeDialog = new GenerateCodeDialog(selectDbTable);
        Content generateCodeContent = contentManager.getFactory().createContent(generateCodeDialog.getContent(), Common.GENERATE_CODE, false);
        contentManager.addContent(generateCodeContent);
        contentManager.setSelectedContent(generateCodeContent);
        toolWindow.show(() -> {
        });
    }
}
