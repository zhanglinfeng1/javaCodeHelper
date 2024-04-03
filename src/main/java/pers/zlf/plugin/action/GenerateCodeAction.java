package pers.zlf.plugin.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.dialog.GenerateCodeDialog;

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
        new GenerateCodeDialog(selectDbTable).open();
    }
}
