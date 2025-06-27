package pers.zlf.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.schedule.NewCodeRemind;
import pers.zlf.plugin.schedule.ZenTaoRemind;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 23:58
 */
public class ScheduledTasksListener implements StartupActivity {
    private static ScheduledExecutorService newCodeExecutorService = Executors.newScheduledThreadPool(1);
    private static ScheduledExecutorService zenTaoExecutorService = Executors.newScheduledThreadPool(1);
    private static NewCodeRemind newCodeRemind;
    private static ZenTaoRemind zenTaoRemind;

    @Override
    public void runActivity(@NotNull Project project) {
        newCodeRemind = new NewCodeRemind(project);
        zenTaoRemind = new ZenTaoRemind(project);
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
        if (config.isOpenCodeRemind()) {
            // 延迟1分钟后开始执行任务，然后每隔10分钟执行一次
            newCodeExecutorService.scheduleAtFixedRate(newCodeRemind, 10, config.getCodeRemindMinute() * 60L, TimeUnit.SECONDS);
        }
        if (config.isOpenZenTaoRemind()) {
            // 延迟1分钟后开始执行任务，然后每隔10分钟执行一次
            zenTaoExecutorService.scheduleAtFixedRate(zenTaoRemind, 10, config.getZenTaoRemindMinute() * 60L, TimeUnit.SECONDS);
        }
    }

    /**
     * 立即关闭
     */
    public static void shutdownNowNewCodeRemind() {
        if (newCodeExecutorService.isShutdown()) {
            newCodeExecutorService.shutdownNow();
        }
    }

    /**
     * 立即关闭
     */
    public static void shutdownZenTaoRemind() {
        if (zenTaoExecutorService.isShutdown()) {
            zenTaoExecutorService.shutdownNow();
        }
    }

    /**
     * 重新时执行
     *
     * @param minute 间隔分钟
     */
    public static void rerunNewCodeRemind(int minute) {
        shutdownNowNewCodeRemind();
        newCodeExecutorService = Executors.newScheduledThreadPool(1);
        newCodeExecutorService.scheduleWithFixedDelay(newCodeRemind, 10, minute * 60L, TimeUnit.SECONDS);
    }

    /**
     * 重新时执行
     *
     * @param minute 间隔分钟
     */
    public static void rerunZenTaoRemind(int minute) {
        shutdownNowNewCodeRemind();
        zenTaoExecutorService = Executors.newScheduledThreadPool(1);
        zenTaoExecutorService.scheduleWithFixedDelay(zenTaoRemind, 10, minute * 60L, TimeUnit.SECONDS);
    }

    /**
     * 启动完成
     *
     * @return boolean
     */
    public static boolean isStartupCompleted() {
        return newCodeRemind != null && zenTaoRemind != null;
    }
}
