package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/26 18:20
 */
public class ReadFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SqlToolWindow sqlToolWindow = new SqlToolWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(sqlToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
