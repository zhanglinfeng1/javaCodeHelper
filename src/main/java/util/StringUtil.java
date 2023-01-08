package util;

import constant.COMMON;
import constant.REGEX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static String toString(Object obj) {
        if (null == obj) {
            return COMMON.BLANK_STRING;
        }
        return obj.toString();
    }

    public static boolean isEmpty(Object obj) {
        String str = toString(obj);
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(toString(obj));
    }

    /**
     * 首字母小写
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toLowerCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] <= 90) {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }

    /**
     * 首字母大写
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toUpperCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] > 90) {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static boolean isEnglish(String text) {
        return text.getBytes().length == text.length();
    }

    /**
     * 正则获取首个匹配的字符串
     *
     * @param str   待处理字符串
     * @param regex 正则表达式
     * @return String
     */
    public static String getFirstMatcher(String str, String regex) {
        Matcher m = Pattern.compile(regex).matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return COMMON.BLANK_STRING;
    }

    /**
     * 转驼峰格式
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toHumpStyle(String str) {
        Pattern compile = Pattern.compile(REGEX.HUMP);
        Matcher matcher = compile.matcher(str.toLowerCase());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(0).toUpperCase().replace(COMMON.UNDERSCORE, COMMON.BLANK_STRING));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
