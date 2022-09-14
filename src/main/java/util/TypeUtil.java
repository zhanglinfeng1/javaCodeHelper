package util;

import constant.COMMON_CONSTANT;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeUtil {

    public static String toJavaType(String sqlType) {
        if (sqlType.contains("int")) {
            return "Integer";
        } else if (sqlType.contains("timestamp")) {
            return "Timestamp";
        } else if (sqlType.contains("double")) {
            return "Double";
        } else {
            return "String";
        }
    }

    public static boolean isObject(String type){
        return !COMMON_CONSTANT.BASIC_TYPE_LIST.contains(type) && !COMMON_CONSTANT.COMMON_TYPE_LIST.contains(type);
    }

}
