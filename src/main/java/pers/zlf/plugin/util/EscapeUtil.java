package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/11/25 10:17
 */
public class EscapeUtil {

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

}
