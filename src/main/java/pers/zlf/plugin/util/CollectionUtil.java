package pers.zlf.plugin.util;

import java.util.Collection;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/13 10:36
 */
public class CollectionUtil {

    /**
     * 判断集合是否相等
     *
     * @param collection1 集合1
     * @param collection2 集合2
     * @return boolean
     */
    public static <T> boolean equals(Collection<T> collection1, Collection<T> collection2) {
        if (null == collection1 || null == collection2) {
            return false;
        }
        return collection1.size() == collection2.size() && collection1.containsAll(collection2);
    }

}
