package pers.zlf.plugin.schedule;

import com.intellij.openapi.project.Project;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 22:51
 */
public abstract class Schedule implements Runnable {
    protected final Project PROJECT;
    private final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> future;

    public Schedule(Project project) {
        this.PROJECT = project;
        if (isRun()) {
            // 延迟10秒后开始执行任务
            future = SERVICE.scheduleAtFixedRate(this, 10, getRemindMinute() * 60L, TimeUnit.SECONDS);
        }
    }

    protected abstract boolean isRun();

    protected abstract int getRemindMinute();

    /**
     * 刷新定时任务
     */
    public void refresh() {
        if (isRun()) {
            Optional.ofNullable(future).ifPresent(t -> future.cancel(true));
            future = SERVICE.scheduleWithFixedDelay(this, 10, getRemindMinute() * 60L, TimeUnit.SECONDS);
        } else {
            Optional.ofNullable(future).ifPresent(t -> future.cancel(true));
        }
    }
}
