package pers.zlf.plugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CommonToolsDialog;

/**
 * @author zhanglinfeng
 * @date create in 2022/8/26 18:20
 */
public class CodeToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        //常用工具窗口
        Content commonToolsContent = contentManager.getFactory().createContent(new CommonToolsDialog().getContent(), Common.COMMON_TOOLS, false);
        contentManager.addContent(commonToolsContent);
    }

}
