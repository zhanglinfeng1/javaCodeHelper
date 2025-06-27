package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.schedule.NewCodeRemindSchedule;
import pers.zlf.plugin.schedule.Schedule;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/27 17:33
 */
public class NewCodeRemindListener extends ScheduledTasksListener {

    @Override
    protected Schedule getSchedule(Project project) {
        return new NewCodeRemindSchedule(project);
    }

    @Override
    protected boolean isRun() {
        return ConfigFactory.getInstance().getCommonConfig().isOpenCodeRemind();
    }

    @Override
    protected int getRemindMinute() {
        return ConfigFactory.getInstance().getCommonConfig().getCodeRemindMinute();
    }
}
