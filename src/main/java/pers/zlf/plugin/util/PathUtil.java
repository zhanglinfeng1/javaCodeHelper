package pers.zlf.plugin.util;

import java.nio.file.Path;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/25 16:18
 */
public class PathUtil {

    /**
     * 统一路径格式
     *
     * @param path 路径
     * @return 格式化后的路径
     */
    public static String format(String path) {
        return Path.of(path).toString();
    }
}
