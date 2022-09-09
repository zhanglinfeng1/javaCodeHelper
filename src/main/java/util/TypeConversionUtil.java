package util;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeConversionUtil {

    public static String conversion(String sqlType) {
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

}
