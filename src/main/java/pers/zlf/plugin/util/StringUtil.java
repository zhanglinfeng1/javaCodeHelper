package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.CommentFormat;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static String toString(Object obj) {
        return Optional.ofNullable(obj).orElse(Common.BLANK_STRING).toString();
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
        return m.find() ? m.group(1) : Common.BLANK_STRING;
    }

    /**
     * 转驼峰格式
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toHumpStyle(String str) {
        if (!str.contains(Common.UNDERSCORE)) {
            return str;
        }
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

    /**
     * 判断是否为注释行
     *
     * @param line               待判断行
     * @param commentFormat      注释格式
     * @return boolean
     */
    public static boolean isComment(String line, CommentFormat commentFormat) {
        // 前缀匹配
        if (CollectionUtil.isNotEmpty(commentFormat.getCommentPrefixList()) && commentFormat.getCommentPrefixList().stream().anyMatch(line::startsWith)) {
            commentFormat.setParagraphComment(true);
            return true;
        }
        // 后缀匹配
        if (CollectionUtil.isNotEmpty(commentFormat.getCommentSuffixList()) && commentFormat.getCommentSuffixList().stream().anyMatch(line::endsWith)) {
            commentFormat.setParagraphComment(false);
            return true;
        }
        // 在段落注释中间 、 单行注释
        return commentFormat.isParagraphComment() || (CollectionUtil.isNotEmpty(commentFormat.getCommentList()) && commentFormat.getCommentList().stream().anyMatch(line::startsWith));
    }
}
