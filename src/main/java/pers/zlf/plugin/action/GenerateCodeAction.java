package pers.zlf.plugin.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.GenerateCodeDialog;
import pers.zlf.plugin.factory.ConfigFactory;

import java.util.Map;

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
        //生成代码的窗口
        Map<String, Map<String, String>> totalTemplateMap = ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap();
        if (totalTemplateMap == null || totalTemplateMap.isEmpty()){
            Messages.showMessageDialog(Message.TEMPLATE_CONFIGURATION, Common.BLANK_STRING, Icon.LOGO);
            return;
        }
        new GenerateCodeDialog(project, selectDbTable).show();
    }
}
