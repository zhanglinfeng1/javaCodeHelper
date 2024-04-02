package pers.zlf.plugin.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.GenerateCodeDialog;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/19 18:13
 */
public class GenerateCodeAction extends BaseAction {
    /** 选中的表 */
    private DbTable selectDbTable;
    /** 工具窗口 */
    private ToolWindow toolWindow;

    @Override
    public boolean isVisible() {
        toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Common.JAVA_CODE_HELPER);
        if (toolWindow == null) {
            return false;
        }
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
        GenerateCodeDialog.getInstance().initTableInfo(selectDbTable);
        toolWindow.getContentManager().setSelectedContent(toolWindow.getContentManager().getContent(1));
        toolWindow.show(() -> {
        });
    }
}
