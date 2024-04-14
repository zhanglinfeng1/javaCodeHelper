package pers.zlf.plugin.util;

import java.util.Map;
import java.util.Set;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/10 9:49
 */
public class MapUtil {

    /**
     * 判断Map是否相等
     *
     * @param map1 Map1
     * @param map2 Map2
     * @return boolean
     */
    public static <T, R> boolean equals(Map<T, R> map1, Map<T, R> map2) {
        if (null == map1 || null == map2) {
            return false;
        }
        Set<T> set1 = map1.keySet();
        Set<T> set2 = map2.keySet();
        if (!CollectionUtil.equals(set1, set2)) {
            return false;
        }
        for (T key : set1) {
            R value1 = map1.get(key);
            R value2 = map2.get(key);
            if (!StringUtil.toString(value1).equals(StringUtil.toString(value2))) {
                return false;
            }
        }
        return true;
    }
}
