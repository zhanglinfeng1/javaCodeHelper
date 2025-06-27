package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.schedule.Schedule;
import pers.zlf.plugin.schedule.ZenTaoRemindSchedule;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/27 17:33
 */
public class ZenTaoRemindListener extends ScheduledTasksListener {

    @Override
    protected Schedule getSchedule(Project project) {
        return new ZenTaoRemindSchedule(project);
    }

    @Override
    protected boolean isRun() {
        return ConfigFactory.getInstance().getCommonConfig().isOpenZenTaoRemind();
    }

    @Override
    protected int getRemindMinute() {
        return ConfigFactory.getInstance().getCommonConfig().getZenTaoRemindMinute();
    }
}
