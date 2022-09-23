package util;

import constant.COMMON_CONSTANT;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeUtil {

    public static String toJavaType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.contains("int") || sqlType.contains("integer")) {
            return "Integer";
        } else if (sqlType.contains("timestamp") || sqlType.contains("date") || sqlType.contains("datetime") || sqlType.contains("time")) {
            return "Timestamp";
        } else if (sqlType.contains("double") || sqlType.contains("float") || sqlType.contains("decimal") || sqlType.contains("number")) {
            return "Double";
        } else {
            return "String";
        }
    }

    public static boolean isObject(String type) {
        return !COMMON_CONSTANT.BASIC_TYPE_LIST.contains(type) && !COMMON_CONSTANT.COMMON_TYPE_LIST.contains(type);
    }

}
