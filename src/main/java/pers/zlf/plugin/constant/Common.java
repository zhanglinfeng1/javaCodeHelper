package pers.zlf.plugin.constant;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 17:52
 */
public class Common {
    /** 通用常量 */
    public static final String TRUE = "true";
    public static final String BLANK_STRING = "";
    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    public static final String LEFT_BRACKETS = "[";
    public static final String RIGHT_BRACKETS = "]";
    public static final String LEFT_PARENTHESES = "(";
    public static final String RIGHT_PARENTHESES = ")";
    public static final String LEFT_ANGLE_BRACKET = "<";
    public static final String RIGHT_ANGLE_BRACKET = ">";
    public static final String UNDERLINE = "_";
    public static final String SPACE = " ";
    public static final String PERCENT_SIGN = "%";
    public static final String HASH_LEFT_BRACE = "#{";
    public static final String DOLLAR_LEFT_BRACE = "${";
    public static final String T = "t";
    public static final String CHARSET_UTF_8 = "utf-8";
    public static final String IMAGE_JPG = "JPG";
    public static final String HOOK_UP = "√";
    public static final String FONT_WRYH = "微软雅黑";
    public static final String LINE_BREAK = "\n";

    /** FreeMark模板 */
    public static final String TEMPLATE_PATH = "templates";
    public static final String JUMP_TO_METHOD_TEMPLATE = "JumpToMethod.ftl";
    public static final String JUMP_TO_XML_TEMPLATE = "JumpToXml.ftl";
    public static final List<String> TEMPLATE_LIST = List.of("Model.java.ftl", "VO.java.ftl", "Mapper.java.ftl", "Mapper.xml.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "Controller.java.ftl");
    public static final String DEFAULT_TEMPLATE = "默认模版";
    public static final String MODEL = "Model";
    public static final String ID = "id";

    /** 配置相关 */
    public static final String JAVA_CODE_HELPER = "JavaCodeHelper";
    public static final String CODE_STATISTICS_DETAILS = "Code statistics details";
    public static final String FAST_JUMP = "快捷跳转";
    public static final String CODE_STATISTICS = "代码统计";
    public static final String SELECT_MODULE = "选择模块";
    public static final String TEMPLATE_CONFIG = "模版配置";
    public static final String COMMON_TOOLS = "常用工具";
    public static final String GENERATE_CODE = "生成代码";
    public static final String RAINBOW_BRACKET = "彩虹括号";
    public static final String ANGLE_BRACKET_COLOR_KEY = "JAVA_CODE_HELPER_ANGLE_BRACKET_COLOR";
    public static final String PARENTH_COLOR_KEY = "JAVA_CODE_HELPER_PARENTH_COLOR";
    public static final String BRACKET_COLOR_KEY = "JAVA_CODE_HELPER_BRACKET_COLOR";
    public static final String BRACE_COLOR_KEY = "JAVA_CODE_HELPER_BRACE_COLOR";
    public static final String ANGLE_BRACKET_COLOR_DISPLAY_NAME = "尖括号//颜色";
    public static final String PARENTH_COLOR_DISPLAY_NAME = "小括号//颜色";
    public static final String BRACKET_COLOR_DISPLAY_NAME = "中括号//颜色";
    public static final String BRACE_COLOR_DISPLAY_NAME = "大括号//颜色";
    public static final String ANGLE_BRACKET_COLOR_TAG_NAME = "JKH";
    public static final String PARENTH_COLOR_TAG_NAME = "XKH";
    public static final String BRACKET_COLOR_TAG_NAME = "ZKH";
    public static final String BRACE_COLOR_TAG_NAME = "DKH";

    /** 翻译 */
    public static final Integer BAIDU_TRANSLATE = 0;

    /** 接口文档Api */
    public static final Integer SWAGGER2_API = 0;
    public static final Integer SWAGGER3_API = 1;

    /** 用于拼接的字符串 */
    public static final String GET = "get";
    public static final String SET = "set";
    public static final String IS = "is";
    public static final String EQ_STR = " = ";
    public static final String END_STR = "(%s);";
    public static final String FILLING_STR = "(%s = \"%s\")";
    public static final String FILLING_STR2 = "(%s = \"%s\", %s = %s)";
    public static final String FILLING_STR3 = "(%s = %s)";
    public static final String ARRAYS_STREAM_STR = "Arrays.stream(%s).map(";
    public static final String STREAM_MAP_STR = ".stream().map(";
    public static final String COLLECT_LIST_STR = "::new).collect(Collectors.toList());";
    public static final String COLLECT_SET_STR = "::new).collect(Collectors.toSet());";
    public static final String S_STR = "Arr";
    public static final String CONSTRUCTOR_FILL_STR1 = "this.%s=%s;";
    public static final String CONSTRUCTOR_FILL_STR2 = "this.%s=%s.get%s();";
    public static final String OPTIONAL = "Optional.ofNullable(%s)";
    public static final String OPTIONAL_THROW = ".orElseThrow(()->%s);";
    public static final String OPTIONAL_ELSE = ".orElse(%s)";
    public static final String LAMBDA_STR = "(%s -> %s)";
    public static final String LAMBDA_SIMPLIFY_STR = "(%s::%s)";
    public static final String MAP_STR = ".map";
    public static final String IF_PRESENT_STR = ".ifPresent";
    public static final String COLOR_TAG_STR = "<%s>%s </%s>";

    /** 目录名 */
    public static final String JAVA = "java";
    public static final String RESOURCES = "resources";
    public static final String SRC = "src";
    public static final String SRC_MAIN_JAVA = "src%smain%sjava";
    public static final String DOT_GIT = ".git";

    /** 常用加解密方式 */
    public static final String BASE64 = "BASE64";
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String SHA256 = "SHA256";
    public static final String SHA512 = "SHA512";
    public static final String AES = "AES";
    public static final String AES_ECB = "AES_ECB";
    public static final String AES_CBC = "AES_CBC";

}
