package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/20 11:48
 */
public class DateUtil {
    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static String nowStr(String formatType) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatType));
    }
}
