package pers.zlf.plugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CommonToolsDialog;
import pers.zlf.plugin.dialog.GenerateCodeDialog;

/**
 * @author zhanglinfeng
 * @date create in 2022/8/26 18:20
 */
public class CodeToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull com.intellij.openapi.wm.ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        //常用工具窗口
        Content commonToolsContent = contentManager.getFactory().createContent(new CommonToolsDialog().getContent(), Common.COMMON_TOOLS, false);
        //生成代码的窗口
        Content generateCodeContent = contentManager.getFactory().createContent(new GenerateCodeDialog().getContent(), Common.GENERATE_CODE, false);
        contentManager.addContent(commonToolsContent);
        contentManager.addContent(generateCodeContent);
    }

}
