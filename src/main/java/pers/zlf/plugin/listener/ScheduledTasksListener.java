package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.schedule.Schedule;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 23:58
 */
public abstract class ScheduledTasksListener implements StartupActivity {
    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> future = null;
    private static Schedule schedule;

    @Override
    public void runActivity(@NotNull Project project) {
        schedule = getSchedule(project);
        if (isRun()) {
            // 延迟10秒后开始执行任务
            future = SERVICE.scheduleAtFixedRate(schedule, 10, getRemindMinute() * 60L, TimeUnit.SECONDS);
        }
    }

    protected abstract Schedule getSchedule(Project project);

    protected abstract boolean isRun();

    protected abstract int getRemindMinute();

    /**
     * 刷新定时任务
     *
     * @param open   开启定时任务
     * @param minute 轮循序时间
     */
    public static void refresh(boolean open, int minute) {
        if (schedule == null) {
            return;
        }
        if (open) {
            Optional.ofNullable(future).ifPresent(t -> future.cancel(true));
            future = SERVICE.scheduleWithFixedDelay(schedule, 10, minute * 60L, TimeUnit.SECONDS);
        } else {
            Optional.ofNullable(future).ifPresent(t -> future.cancel(true));
        }
    }

}
