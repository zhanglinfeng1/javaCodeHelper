package factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/12/22 17:51
 */
public class ThreadPoolFactory {
    public static final ThreadPoolExecutor TRANS_POOL = new ThreadPoolExecutor(5, 10, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("trans-pool-%d").build());

}
