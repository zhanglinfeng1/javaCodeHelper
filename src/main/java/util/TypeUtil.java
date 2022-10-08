package util;

import constant.COMMON_CONSTANT;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeUtil {

    public static boolean isObject(String type) {
        return !COMMON_CONSTANT.BASIC_TYPE_LIST.contains(type) && !COMMON_CONSTANT.COMMON_TYPE_LIST.contains(type);
    }

}
