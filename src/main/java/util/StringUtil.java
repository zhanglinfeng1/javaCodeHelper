package util;

import constant.COMMON_CONSTANT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static String toString(Object obj) {
        if (null == obj) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        return String.valueOf(obj);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String toLowerCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] <= 90) {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }

    public static String toUpperCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] > 90) {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static boolean isEnglish(String text) {
        byte[] bytes = text.getBytes();
        return bytes.length == text.length();
    }

    public static String getFirstMatcher(String str, String regex) {
        Matcher m = Pattern.compile(regex).matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return COMMON_CONSTANT.BLANK_STRING;
    }
}
