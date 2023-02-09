package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.COMMON;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static String toString(Object obj) {
        return Optional.ofNullable(obj).orElse(COMMON.BLANK_STRING).toString();
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof Collection<?>){
            Collection<?> col = (Collection<?>) obj;
            return col.isEmpty();
        }
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
        if (isEmpty(str)) {
            return str;
        }
        char[] ch = str.toCharArray();
        if (ch[0] >= 65 && ch[0] <= 90) {
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
        if (isEmpty(str)) {
            return str;
        }
        char[] ch = str.toCharArray();
        if (ch[0] >= 97 && ch[0] <= 122) {
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
        return m.find() ? m.group(1) : COMMON.BLANK_STRING;
    }

    /**
     * 转驼峰格式
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toHumpStyle(String str) {
        StringBuilder result = new StringBuilder();
        char[] chars = str.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String charStr = String.valueOf(chars[i]);
            if (chars[i] == 95) {
                i++;
                charStr = String.valueOf(chars[i]).toUpperCase();
            }
            result.append(charStr);
        }
        return result.toString();
    }
}
