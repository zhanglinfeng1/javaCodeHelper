package pers.zlf.plugin.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinfeng
 * @date create in 2022/12/22 17:51
 */
public class ThreadPoolFactory {
    public static final ThreadPoolExecutor TRANS_POOL = new ThreadPoolExecutor(3, 6, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("trans-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());

    public static final ThreadPoolExecutor CODE_STATISTICS_POOL = new ThreadPoolExecutor(3, 12, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("codeStatistics-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());
}
