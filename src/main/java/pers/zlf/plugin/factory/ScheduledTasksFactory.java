package pers.zlf.plugin.factory;

import com.intellij.openapi.project.Project;
import pers.zlf.plugin.schedule.NewCodeRemindSchedule;
import pers.zlf.plugin.schedule.Schedule;
import pers.zlf.plugin.schedule.ZenTaoRemindSchedule;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2025/7/2 22:09
 */
public class ScheduledTasksFactory {
    private static volatile ScheduledTasksFactory scheduledTasksFactory;
    private NewCodeRemindSchedule newCodeRemindSchedule;
    private ZenTaoRemindSchedule zenTaoRemindSchedule;

    private ScheduledTasksFactory() {
    }

    public static ScheduledTasksFactory getInstance() {
        if (scheduledTasksFactory == null) {
            synchronized (ConfigFactory.class) {
                Equals.of(scheduledTasksFactory == null).ifTrue(() -> scheduledTasksFactory = new ScheduledTasksFactory());
            }
        }
        return scheduledTasksFactory;
    }

    public void create(Project project) {
        newCodeRemindSchedule = new NewCodeRemindSchedule(project);
        zenTaoRemindSchedule = new ZenTaoRemindSchedule(project);
    }

    public void refresh() {
        Optional.ofNullable(newCodeRemindSchedule).ifPresent(Schedule::refresh);
        Optional.ofNullable(zenTaoRemindSchedule).ifPresent(Schedule::refresh);
    }

}
