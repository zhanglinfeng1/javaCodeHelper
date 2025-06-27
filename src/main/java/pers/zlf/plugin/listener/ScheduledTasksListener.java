package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.schedule.Schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 23:58
 */
public abstract class ScheduledTasksListener implements StartupActivity {
    protected static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    protected static Schedule schedule;

    @Override
    public void runActivity(@NotNull Project project) {
        schedule = getSchedule(project);
        if (isRun()) {
            // 延迟10秒后开始执行任务
            executorService.scheduleAtFixedRate(schedule, 10, getRemindMinute() * 60L, TimeUnit.SECONDS);
        }
    }

    protected abstract Schedule getSchedule(Project project);

    protected abstract boolean isRun();

    protected abstract int getRemindMinute();

    /**
     * 立即关闭
     */
    public static void shutdown() {
        if (executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    /**
     * 重新时执行
     *
     * @param minute 间隔分钟
     */
    public static void rerun(int minute) {
        shutdown();
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(schedule, 10, minute * 60L, TimeUnit.SECONDS);
    }

    /**
     * 启动完成
     *
     * @return boolean
     */
    public static boolean isStartupCompleted() {
        return schedule != null;
    }
}
