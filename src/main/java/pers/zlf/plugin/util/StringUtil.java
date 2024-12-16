package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static String toString(Object obj) {
        return Optional.ofNullable(obj).orElse(Common.BLANK_STRING).toString();
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof Collection<?> col) {
            return col.isEmpty();
        }
        String str = toString(obj);
        return str == null || str.trim().isEmpty();
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
        if (isUppercaseLetters(ch[0])) {
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
        if (isLowercaseLetters(ch[0])) {
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
        StringBuilder result = new StringBuilder();
        boolean upperCase = false;
        for (char aChar : str.toLowerCase().toCharArray()) {
            String charStr = String.valueOf(aChar);
            if (isNum(aChar) || isLowercaseLetters(aChar) || isUppercaseLetters(aChar)) {
                if (upperCase) {
                    charStr = charStr.toUpperCase();
                }
                result.append(charStr);
                upperCase = false;
                continue;
            }
            upperCase = true;
        }
        return result.toString();
    }

    /**
     * 转下划线格式
     *
     * @param str 待处理字符串
     * @return String
     */
    public static String toUnderlineStyle(String str) {
        StringBuilder result = new StringBuilder();
        for (char aChar : str.toCharArray()) {
            String charStr = String.valueOf(aChar);
            if (isUppercaseLetters(aChar)) {
                Empty.of(result).ifPresent(t -> t.append(Common.UNDERLINE));
                result.append(charStr.toLowerCase());
            }
            if (isNum(aChar) || isLowercaseLetters(aChar)) {
                result.append(charStr);
            }
        }
        return result.toString();
    }

    /**
     * 判断是否为注释行
     *
     * @param line          待判断行
     * @param commentFormat 注释格式
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

    /**
     * 判断是否为关键字
     *
     * @param fileType 文件类型
     * @param line     待判断行
     * @return boolean
     */
    public static boolean isKeyWord(String fileType, String line) {
        if (FileType.JAVA_FILE.equals(fileType)) {
            return line.startsWith(Keyword.JAVA_PACKAGE + Common.SPACE) || line.startsWith(Keyword.JAVA_IMPORT + Common.SPACE);
        }
        return false;
    }

    /**
     * 判断是否为数字
     *
     * @param aChar char
     * @return boolean
     */
    public static boolean isNum(char aChar) {
        return aChar >= 48 && aChar <= 57;
    }

    /**
     * 判断是否为小写字母
     *
     * @param aChar char
     * @return boolean
     */
    public static boolean isLowercaseLetters(char aChar) {
        return aChar >= 97 && aChar <= 122;
    }

    /**
     * 判断是否为大写字母
     *
     * @param aChar char
     * @return boolean
     */
    public static boolean isUppercaseLetters(char aChar) {
        return aChar >= 65 && aChar <= 90;
    }

    /**
     * 中文转unicode
     *
     * @param value 待处理字符串
     * @return String
     */
    public static String unicodeEncode(String value) {
        return IntStream.range(0, value.length()).map(value::charAt).mapToObj(c -> "\\u" + String.format("%04x", c)).collect(Collectors.joining());
    }

    /**
     * unicode转中文
     *
     * @param value 待处理字符串
     * @return String
     */
    public static String unicodeDecode(String value) {
        return Arrays.stream(value.split("\\\\u")).filter(StringUtil::isNotEmpty).filter(t -> t.length() >= 4).map(t -> {
            String num = t.substring(0, 4);
            String other = t.substring(4);
            try {
                return StringUtil.toString((char) Integer.parseInt(num, 16)) + other;
            } catch (Exception e) {
                return num + other;
            }
        }).collect(Collectors.joining());
    }

    /**
     * 替换
     *
     * @param code         代码
     * @param replacedText 被替换代码
     * @param replaceText  替换代码
     * @return String
     */
    public static String codeReplace(String code, String replacedText, String replaceText) {
        List<Character> characterList = new ArrayList<>();
        for (char c : ".,(){}<>[];: ?=".toCharArray()) {
            characterList.add(c);
        }
        StringBuilder result = new StringBuilder(code);
        int index = 0;
        while (true) {
            index = result.indexOf(replacedText, index);
            if (index == -1) {
                break;
            }
            char frontChar = index == 0 ? 95 : code.charAt(index - 1);
            char afterChar = code.charAt(index + replacedText.length());
            if (characterList.contains(frontChar) && characterList.contains(afterChar)) {
                result.replace(index, index + replacedText.length(), replaceText);
            }
            index++;
        }
        return result.toString();
    }

    /**
     * 字符串 转 ascii码
     *
     * @param str 字符串
     * @return ascii码
     */
    public static String toAscii(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            result.append((int) c).append(Common.SPACE);
        }
        return result.toString();
    }

    /**
     * ascii码 转 字符串
     *
     * @param str ascii码
     * @return 字符串
     */
    public static String asciiToString(String str) {
        return Arrays.stream(str.split(Common.SPACE)).filter(StringUtil::isNotEmpty).map(t -> {
            try {
                char c = (char) Integer.parseInt(t);
                return Character.toString(c);
            } catch (Exception e) {
                return t;
            }
        }).collect(Collectors.joining());
    }

    /**
     * 比较2个字符串是否相等，都为空时，返回true
     *
     * @param value1 字符串1
     * @param value2 字符串2
     * @return boolean
     */
    public static boolean equals(String value1, String value2) {
        if (isEmpty(value1) && isEmpty(value2)) {
            return true;
        } else if (isEmpty(value1)) {
            return value2.equals(value1);
        } else {
            return value1.equals(value2);
        }
    }
}
