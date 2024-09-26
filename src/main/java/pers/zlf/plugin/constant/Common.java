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

    /** 占位符 */
    public static final String FULL_PATH_INPUT_PLACEHOLDER = "C:\\workspace\\javaCodeHelper\\src\\main\\java\\pers\\zlf\\plugin";
    public static final String PACKAGR_PATH_INPUT_PLACEHOLDER = "pers.zlf.plugin";

    /** 配置相关 */
    public static final String JAVA_CODE_HELPER = "JavaCodeHelper";
    public static final String FAST_JUMP = "快捷跳转";
    public static final String CODE_STATISTICS = "代码统计";
    public static final String SELECT_MODULE = "选择模块";
    public static final String TEMPLATE_CONFIG = "模版配置";
    public static final String COMMON_TOOLS = "常用工具";
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
    /** 表头 */
    public static final String FILE_TYPE_TABLE_HEADER = "参与统计的文件类型(文件后缀，例如：.java)";
    public static final String GIT_EMAIL_TABLE_HEADER = "参与统计贡献率的git邮箱(默认当前邮箱)";
    public static final String[] QUERY_COLUMN_TABLE_HEADER = {"字段名", "别名", "查询方式"};
    public static final String[] DB_TABLE_HEADER = {"字段名", "别名", "类型", "java数据类型", "备注"};
    /** 查询方式下拉选项 */
    public static final String[] SELECT_OPTIONS = {"=", ">", ">=", "<", "<=", "in", "not in", "like", "not like"};
    public static final String[] DATA_TYPE_OPTIONS = {"String", "int", "Integer", "double", "Double", "Date", "Timestamp", "LocalDateTime"};
    /** 翻译 */
    public static final Integer BAIDU_TRANSLATE = 0;
    public static final String BAIDU_TRANSLATE_CHINESE = "百度翻译";
    public static final String BAIDU_TRANSLATE_URL = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&salt=%s&sign=%s&appid=%s";
    /** 接口文档Api */
    public static final Integer SWAGGER2_API = 0;
    public static final Integer SWAGGER3_API = 1;

    /** 用于拼接的字符串 */
    public static final String GET = "get";
    public static final String SET = "set";
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
    /** 构造方法补全提示 */
    public static final String FILL_CONSTRUCTOR = "补全构造方法";

    /** 注释前缀、后缀 */
    public static final List<String> JAVA_COMMENT = List.of("//");
    public static final List<String> JAVA_COMMENT_PREFIX = List.of("/*");
    public static final List<String> JAVA_COMMENT_SUFFIX = List.of("*/");
    public static final List<String> XML_COMMENT_PREFIX = List.of("<!--", "<![CDATA[");
    public static final List<String> XML_COMMENT_SUFFIX = List.of("-->", "]]>");

    /** 目录名 */
    public static final String JAVA = "java";
    public static final String RESOURCES = "resources";
    public static final String DOT_GIT = ".git";
    public static final String SRC = "src";
    public static final String SRC_MAIN_JAVA = "src%smain%sjava";

    /** git配置参数 */
    public static final String USER = "user";
    public static final String EMAIL = "email";

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
