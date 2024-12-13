package pers.zlf.plugin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/20 11:48
 */
public class DateUtil {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DDHHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    /**
     * 获取当前时间字符串
     *
     * @param formatType 格式
     * @return String
     */
    public static String nowStr(String formatType) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatType));
    }

    /**
     * 毫秒转字符串
     *
     * @param timestamp  毫秒
     * @param formatType 格式
     * @return String
     */
    public static String millisecondsToString(long timestamp, String formatType) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(formatType));
    }

    /**
     * 秒转字符串
     *
     * @param timestamp  秒
     * @param formatType 格式
     * @return String
     */
    public static String secondsToString(long timestamp, String formatType) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(formatType));
    }

    /**
     * 字符串转毫秒
     *
     * @param timestamp 日期字符串转
     * @return String
     */
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

    /**
     * 字符串转Date
     *
     * @param dateStr    字符串
     * @param formatType 格式
     * @return Date
     */
    public static Date parse(String dateStr, String formatType) {
        if (StringUtil.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatType);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

}
