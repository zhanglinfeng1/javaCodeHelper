package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.factory.ScheduledTasksFactory;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 23:58
 */
public class ScheduledTasksListener implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        ScheduledTasksFactory.getInstance().create(project);
    }

}
