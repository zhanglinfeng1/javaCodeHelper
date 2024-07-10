package pers.zlf.plugin.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/20 11:48
 */
public class DateUtil {
    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DDHHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static String nowStr(String formatType) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatType));
    }

    public static String millisecondsToString(long timestamp, String formatType) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(formatType));
    }

    public static String secondsToString(long timestamp, String formatType) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(formatType));
    }

    public static String stringToMilliseconds(String timestamp) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = timestamp.trim().toCharArray();
        for (char c : chars) {
            if (c >= 48 && c <= 57) {
                stringBuilder.append(c);
            }
        }
        stringBuilder.append("0".repeat(Math.max(0, 17 - stringBuilder.length())));
        LocalDateTime dateTime = LocalDateTime.parse(stringBuilder.toString(), DateTimeFormatter.ofPattern(YYYYMMDDHHMMSSSSS));
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        return String.valueOf(zonedDateTime.toInstant().toEpochMilli());
    }
}
