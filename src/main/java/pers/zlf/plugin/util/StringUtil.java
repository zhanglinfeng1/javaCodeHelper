package pers.zlf.plugin.util;

import org.yaml.snakeyaml.Yaml;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.util.lambda.Empty;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * url encode
     *
     * @param value 待处理字符串
     * @return String
     */
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * url decode
     *
     * @param value 待处理字符串
     * @return String
     */
    public static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 去除html格式
     *
     * @param text 待处理文本
     * @return String
     */
    public static String removeHtmlFormat(String text) {
        return text.replaceAll("<.*?>", "");
    }


    /**
     * Escape编码
     *
     * @param content 被转义的内容
     * @return 编码后的字符串
     */
    public static String escape(CharSequence content) {
        if (StringUtil.isEmpty(content)) {
            return Common.BLANK_STRING;
        }

        StringBuilder result = new StringBuilder();
        char c;
        for (int i = 0; i < content.length(); i++) {
            c = content.charAt(i);
            if (Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c) || "*@-_+./".indexOf(c) > -1) {
                result.append(c);
            } else if (c < 256) {
                result.append(Common.PERCENT_SIGN);
                if (c < 16) {
                    result.append("0");
                }
                result.append(Integer.toString(c, 16));
            } else {
                result.append("%u");
                if (c <= 0xfff) {
                    result.append("0");
                }
                result.append(Integer.toString(c, 16));
            }
        }
        return result.toString();
    }

    /**
     * Escape解码
     *
     * @param content 被转义的内容
     * @return 解码后的字符串
     */
    public static String unescape(String content) {
        if (StringUtil.isEmpty(content)) {
            return Common.BLANK_STRING;
        }

        StringBuilder result = new StringBuilder();
        int lastPos = 0;
        int pos;
        char ch;
        while (lastPos < content.length()) {
            pos = content.indexOf(Common.PERCENT_SIGN, lastPos);
            if (pos == lastPos) {
                if (content.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                    result.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                    result.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    result.append(content.substring(lastPos));
                    lastPos = content.length();
                } else {
                    result.append(content, lastPos, pos);
                    lastPos = pos;
                }
            }
        }
        return result.toString();
    }

    /**
     * properties转yaml
     *
     * @param text 待处理文本
     * @return String
     */
    public static String propertiesToYaml(String text) {
        Map<String, Object> propertiesMap = new LinkedHashMap<>();
        Arrays.stream(text.split(Common.LINE_BREAK)).filter(StringUtil::isNotEmpty).forEach(line -> {
            Matcher matcher = Pattern.compile(Regex.KEY_VALUE).matcher(line);
            if (matcher.matches()) {
                propertiesMap.put(matcher.group(1), matcher.group(2));
            }
        });
        Map<String, Object> map = parseToMap(propertiesMap);
        return map2Yaml(map, 0).toString();
    }

    /**
     * yaml转properties
     *
     * @param text 待处理文本
     * @return String
     */
    public static String yamlToProperties(String text) {
        Map<String, Object> propertiesMap = new LinkedHashMap<>();
        Map<String, Object> yamlMap = new Yaml().load(text);
        flattenMap(Common.BLANK_STRING, yamlMap, propertiesMap);
        StringBuffer strBuff = new StringBuffer();
        propertiesMap.forEach((key, value) -> strBuff.append(key)
                .append(Common.EQUAL_SIGN)
                .append(value)
                .append(Common.LINE_BREAK));
        return strBuff.toString();
    }

    private static void flattenMap(String prefix, Map<String, Object> yamlMap, Map<String, Object> treeMap) {
        yamlMap.forEach((key, value) -> {
            String fullKey = prefix + key;
            if (value instanceof LinkedHashMap) {
                flattenMap(fullKey + Common.DOT, (LinkedHashMap) value, treeMap);
            } else if (value instanceof ArrayList) {
                List values = (ArrayList) value;
                for (int i = 0; i < values.size(); i++) {
                    String itemKey = String.format("%s[%d]", fullKey, i);
                    Object itemValue = values.get(i);
                    if (itemValue instanceof String) {
                        treeMap.put(itemKey, itemValue);
                    } else {
                        flattenMap(itemKey + Common.DOT, (LinkedHashMap) itemValue, treeMap);
                    }
                }
            } else {
                treeMap.put(fullKey, value.toString());
            }
        });
    }

    private static Map<String, Object> parseToMap(Map<String, Object> propMap) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (propMap == null || propMap.isEmpty()) {
            return resultMap;
        }
        propMap.forEach((key, value) -> {
            if (key.contains(Common.DOT)) {
                String currentKey = key.substring(0, key.indexOf(Common.DOT));
                if (resultMap.get(currentKey) != null) {
                    return;
                }
                Map<String, Object> childMap = new LinkedHashMap<>();
                propMap.forEach((childKey, childValue) -> {
                    if (childKey.contains(currentKey + Common.DOT)) {
                        childKey = childKey.substring(childKey.indexOf(Common.DOT) + 1);
                        childMap.put(childKey, childValue);
                    }
                });
                Map<String, Object> map = parseToMap(childMap);
                resultMap.put(currentKey, map);
            } else {
                resultMap.put(key, value);
            }
        });
        return resultMap;
    }

    private static StringBuffer map2Yaml(Map<String, Object> propMap, int deep) {
        StringBuffer yamlBuffer = new StringBuffer();
        if (propMap == null || propMap.isEmpty()) {
            return yamlBuffer;
        }
        String space = getSpace(deep);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            Object valObj = entry.getValue();
            if (entry.getKey().contains(Common.LEFT_BRACKETS) && entry.getKey().contains(Common.RIGHT_BRACKETS)) {
                String key = entry.getKey().substring(0, entry.getKey().indexOf(Common.LEFT_BRACKETS)) + Common.COLON;
                yamlBuffer.append(space).append(key).append(Common.LINE_BREAK);
                propMap.forEach((itemKey, itemValue) -> {
                    if (itemKey.startsWith(key.substring(0, entry.getKey().indexOf(Common.LEFT_BRACKETS)))) {
                        yamlBuffer.append(getSpace(deep + 1)).append("- ");
                        if (itemValue instanceof Map) {
                            StringBuffer valStr = map2Yaml((Map<String, Object>) itemValue, 0);
                            String[] split = valStr.toString().split(Common.LINE_BREAK);
                            for (int i = 0; i < split.length; i++) {
                                if (i > 0) {
                                    yamlBuffer.append(getSpace(deep + 2));
                                }
                                yamlBuffer.append(split[i]).append(Common.LINE_BREAK);
                            }
                        } else {
                            yamlBuffer.append(itemValue).append(Common.LINE_BREAK);
                        }
                    }
                });
                break;
            } else {
                String key = space + entry.getKey() + Common.COLON;
                if (valObj instanceof String) {
                    yamlBuffer.append(key).append(Common.SPACE).append(valObj).append(Common.LINE_BREAK);
                } else if (valObj instanceof List) {
                    yamlBuffer.append(key).append(Common.LINE_BREAK);
                    List<String> list = (List<String>) entry.getValue();
                    String lSpace = getSpace(deep + 1);
                    for (String str : list) {
                        yamlBuffer.append(lSpace).append("- ").append(str).append(Common.LINE_BREAK);
                    }
                } else if (valObj instanceof Map) {
                    Map<String, Object> valMap = (Map<String, Object>) valObj;
                    yamlBuffer.append(key).append(Common.LINE_BREAK);
                    StringBuffer valStr = map2Yaml(valMap, deep + 1);
                    yamlBuffer.append(valStr);
                } else {
                    yamlBuffer.append(key).append(Common.SPACE).append(valObj).append(Common.LINE_BREAK);
                }
            }

        }
        return yamlBuffer;
    }

    private static String getSpace(int deep) {
        if (deep == 0) {
            return Common.BLANK_STRING;
        }
        return (Common.SPACE + Common.SPACE).repeat(Math.max(0, deep));
    }

}
