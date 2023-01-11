package pers.zlf.plugin.constant;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/12/2 15:55
 */
public class REGEX {
    public static final String SQL_REPLACE = "[',`]";
    public static final String SPACE = "\\s+";
    public static final String APOSTROPHE = "'(.*?)'";
    public static final String DOT = "\\.";
    public static final String DOUBLE_QUOTES = "\"";
    public static final String WRAP = "[\n\r/*]";
    public static final String PARENTHESES = "<(.*?)>";
    public static final String LEFT_BRACKETS = "\\[";
    public static final String HUMP = "_[a-z]";
}
