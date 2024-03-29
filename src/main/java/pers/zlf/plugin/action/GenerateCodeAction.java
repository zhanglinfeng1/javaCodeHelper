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

/**
 * @author zhanglinfeng
 * @date create in 2024/3/19 18:13
 */
public class GenerateCodeAction extends BaseAction {
    /** 选中的表*/
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
        if (toolWindow == null) {
            return;
        }
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeContent(contentManager.getContent(1), true);
        GenerateCodeDialog generateCodeDialog = new GenerateCodeDialog();
        generateCodeDialog.initTableInfo(selectDbTable);
        Content generateCodeContent = contentManager.getFactory().createContent(generateCodeDialog.getContent(), Common.GENERATE_CODE, false);
        contentManager.addContent(generateCodeContent);
        toolWindow.getContentManager().setSelectedContent(generateCodeContent);
        toolWindow.show(() -> {
        });
    }
}
