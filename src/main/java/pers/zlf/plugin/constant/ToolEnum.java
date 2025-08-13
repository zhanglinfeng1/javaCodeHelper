package pers.zlf.plugin.constant;

import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.function.Function;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/10 13:56
 */
public enum ToolEnum {
    UNICODE("转unicode", "转字符串", StringUtil::unicodeEncode, StringUtil::unicodeDecode),
    URL_ENCODE("encode", "decode", StringUtil::urlEncode, StringUtil::urlDecode),
    ESCAPE("escape", "unescape", StringUtil::escape, StringUtil::unescape),
    TIMESTAMP_CONVERSION("格式化", "转时间戳(毫秒)", DateUtil::toString, DateUtil::stringToMilliseconds),
    ASCII("转ASCII码", "转字符串", StringUtil::toAscii, StringUtil::asciiToString),
    REMOVE_HTML_FORMAT("去除", null, StringUtil::removeHtmlFormat, null);

    public final String downButtonName;
    public final String upButtonName;
    public final Function<String, String> downButtonFunction;
    public final Function<String, String> upButtonFunction;

    ToolEnum(String downButtonName, String upButtonName, Function<String, String> downButtonFunction, Function<String, String> upButtonFunction) {
        this.downButtonName = downButtonName;
        this.upButtonName = upButtonName;
        this.downButtonFunction = downButtonFunction;
        this.upButtonFunction = upButtonFunction;
    }

}
