package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/20 11:48
 */
public class DateUtil {
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static String nowStr(String formatType) {
        return new SimpleDateFormat(formatType).format(new Date());
    }
}
