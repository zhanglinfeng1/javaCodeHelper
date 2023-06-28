package pers.zlf.plugin.util;

import java.nio.file.Path;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/25 16:18
 */
public class PathUtil {

    /**
     * 判断路径中是否包含了文件夹
     *
     * @param pathStr       路径
     * @param directoryName 文件夹名
     * @return boolean
     */
    public static boolean contain(String pathStr, String directoryName) {
        Path path = Path.of(pathStr);
        for (Path subPath : path) {
            if (subPath.endsWith(directoryName)) {
                return true;
            }
        }
        return false;
    }

}
