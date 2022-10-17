package util;

import constant.TYPE_CONSTANT;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeUtil {

    public static boolean isObject(String type) {
        return !TYPE_CONSTANT.BASIC_TYPE_LIST.contains(type) && !TYPE_CONSTANT.COMMON_TYPE_LIST.contains(type);
    }

    public static boolean equals(String firstType, String secondType) {
        return firstType.equals(secondType) || firstType.equals(TYPE_CONSTANT.BASIC_TYPE_MAP.get(secondType));
    }
}
