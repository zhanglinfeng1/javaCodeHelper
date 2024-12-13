package pers.zlf.plugin.action;

import com.intellij.database.model.DasTable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.MyDataKeys;
import pers.zlf.plugin.dialog.GenerateCodeDialog;
import pers.zlf.plugin.factory.ConfigFactory;

import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/19 18:13
 */
public class GenerateCodeAction extends BaseAction {
    /** 选中的表 */
    private DasTable selectDasTable;

    @Override
    public boolean isVisible() {
        //获取选中的元素
        Object[] data = event.getData(MyDataKeys.DATABASE_NODES);
        if (data != null && data.length > 0 && data[0] instanceof DasTable dasTable) {
            selectDasTable = dasTable;
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        //生成代码的窗口
        Map<String, Map<String, String>> totalTemplateMap = ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap();
        if (totalTemplateMap == null || totalTemplateMap.isEmpty()) {
            Message.showMessage(Message.TEMPLATE_CONFIGURATION);
            return;
        }
        ToolWindowManager.getInstance(project).invokeLater(() -> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Common.JAVA_CODE_HELPER);
            ContentManager contentManager = toolWindow.getContentManager();
            for (Content content : contentManager.getContents()) {
                if (!Common.COMMON_TOOLS.equals(content.getDisplayName())) {
                    contentManager.removeContent(content, true);
                }
            }
            String displayName = selectDasTable.getName() + Common.SPACE + Common.GENERATE_CODE;
            Content content = contentManager.getFactory().createContent(new GenerateCodeDialog(project, selectDasTable).getContent(), displayName, false);
            content.setCloseable(true);
            contentManager.addContent(content);
            contentManager.setSelectedContent(content);
            toolWindow.show();
        });
    }

}
