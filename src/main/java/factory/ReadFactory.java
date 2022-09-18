package factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import constant.COMMON_CONSTANT;
import dialog.ToolWindowDialog;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/26 18:20
 */
public class ReadFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        COMMON_CONSTANT.FULL_PATH = project.getBasePath();
        //初始化组件
        ToolWindowDialog dialog = new ToolWindowDialog();
        //添加到IDEA中
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(dialog.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
