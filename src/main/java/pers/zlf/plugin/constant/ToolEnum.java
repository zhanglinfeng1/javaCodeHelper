package pers.zlf.plugin.constant;

import pers.zlf.plugin.util.StringUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/24 14:45
 */
public enum ToolEnum {
    UNICODE("unicode", StringUtil::unicodeEncode, StringUtil::unicodeDecode),
    URL_ENCODE("urlEncode", t -> URLEncoder.encode(t, StandardCharsets.UTF_8), t -> URLDecoder.decode(t, StandardCharsets.UTF_8));

    /** 键 */
    private String key;
    /** 正向方法 */
    private Function<String,String> positive;
    /** 反向方法 */
    private Function<String,String> negative;

    ToolEnum(String key, Function<String, String> positive, Function<String, String> negative) {
        this.key = key;
        this.positive = positive;
        this.negative = negative;
    }

    public static String positive(String key, String value) {
        return Arrays.stream(ToolEnum.values()).filter(t -> t.key.equals(key)).map(t -> t.positive.apply(value)).findAny().orElse(value);
    }

    public static String negative(String key, String value) {
        return Arrays.stream(ToolEnum.values()).filter(t -> t.key.equals(key)).map(t -> t.negative.apply(value)).findAny().orElse(value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Function<String, String> getPositive() {
        return positive;
    }

    public void setPositive(Function<String, String> positive) {
        this.positive = positive;
    }

    public Function<String, String> getNegative() {
        return negative;
    }

    public void setNegative(Function<String, String> negative) {
        this.negative = negative;
    }
}
