package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.ClassType;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 17:37
 */
public class TypeUtil {

    /**
     * 判断是否是简单类型
     *
     * @param typeStr 类型
     * @return boolean
     */
    public static boolean isSimpleType(String typeStr) {
        return ClassType.BASIC_TYPE_LIST.contains(typeStr);
    }
}
