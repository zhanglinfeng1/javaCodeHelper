package pers.zlf.plugin.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 17:52
 */
public class Common {
    /** 通用常量 */
    public static final String SUCCESS = "success";
    public static final String TRUE = "true";
    public static final String BLANK_STRING = "";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String LEFT_BRACE = "{";
    public static final String LEFT_BRACKETS = "[";
    public static final String LEFT_PARENTHESES = "(";
    public static final String RIGHT_PARENTHESES = ")";
    public static final String LESS_THAN_SIGN = "<";
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";
    public static final String DOUBLE_QUOTATION = "\"";
    public static final String PERCENT_SIGN = "%";

    /** FreeMark模板 */
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "templates";
    public static final List<String> TEMPLATE_LIST = List.of(".java.ftl", "Controller.java.ftl", "Mapper.java.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "VO.java.ftl");

    /** 数据库 */
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String POSTGRESQL = "postgresql";
    public static final String TABLE = "TABLE";
    public static final String COMMENT = "COMMENT";
    public static final String CONSTRAINT = "constraint";
    public static final String[] QUERY_COLUMN_TABLE_HEADER = {"字段名", "别名", "查询方式"};
    public static final String[] SELECT_OPTIONS = {"=", ">", ">=", "<", "<=", "in", "not in", "like", "not like"};

    /** GUI */
    public static final String FULL_PATH_INPUT_PLACEHOLDER = "C:\\workspace\\javaCodeHelper\\src\\main\\java\\javaCodeHelperFile";
    public static final String PACKAGR_PATH_INPUT_PLACEHOLDER = "com.zlf.service.impl";
    public static final String CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER = "e.g. C:\\zlf\\CustomTemplate";

    /** 配置 */
    public static final String JAVA_CODE_HELP_CONFIG_XML = "javaCodeHelpConfig.xml";
    public static final String FAST_JUMP_CONFIG_XML = "fastJumpConfig.xml";
    public static final String CODE_STATISTICS_CONFIG_XML = "codeStatisticsConfig.xml";
    public static final String JAVA_CODE_HELP = "JavaCodeHelp";
    public static final String FAST_JUMP = "快捷跳转";
    public static final String CODE_STATISTICS = "代码统计";
    public static final String FILE_TYPE_TABLE_HEADER = "参与统计的文件类型(文件后缀，例如：.java)";
    public static final String GIT_EMAIL_TABLE_HEADER = "参与统计贡献率的git邮箱(默认当前邮箱)";
    public static final String SELECT_MODULE = "选择模块";
    public static final Integer DATE_CLASS_TYPE = 0;
    public static final Map<Integer, String> DATE_TYPE_MAP = new HashMap<>() {{
        put(DATE_CLASS_TYPE, "Date");
        put(1, "Timestamp");
        put(2, "LocalDateTime");
    }};

    /** 用于拼接的字符串 */
    public static final String THIS_STR = "this.";
    public static final String GET = "get";
    public static final String SET = "set";
    public static final String EQ_STR = " = ";
    public static final String END_STR = "(%s);";
    public static final String ARRAYS_STREAM_STR = "Arrays.stream(%s).map(";
    public static final String STREAM_MAP_STR = ".stream().map(";
    public static final String COLLECT_LIST_STR = "::new).collect(Collectors.toList());";
    public static final String COLLECT_SET_STR = "::new).collect(Collectors.toSet());";
    public static final String S_STR = "s";

    /** 翻译 */
    public static final String EN = "en";
    public static final String ZH = "zh";
    public static final Integer BAIDU_TRANSLATE = 0;
    public static final String BAIDU_TRANSLATE_URL = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&salt=%s&sign=%s&appid=%s";
    public static final Map<Integer, String> TRANSLATE_MAP = new HashMap<>() {{
        put(BAIDU_TRANSLATE, "百度翻译");
    }};

    /** Api工具 */
    public static final Integer SWAGGER_API = 0;

    /** 构造方法补全提示 */
    public static final String FILL_CONSTRUCTOR = "补全构造方法";

    /** 注释前缀、后缀 */
    public static final List<String> JAVA_COMMENT = List.of("//");
    public static final List<String> JAVA_COMMENT_PREFIX = List.of("/*");
    public static final List<String> JAVA_COMMENT_SUFFIX = List.of("*/");
    public static final List<String> XML_COMMENT_PREFIX = List.of("<!--", "<![CDATA[");
    public static final List<String> XML_COMMENT_SUFFIX = List.of("-->", "]]>");

    /** test目录 */
    public static final String TEST = "test";
    /** resources目录 */
    public static final String RESOURCES = "resources";
    /** git目录 */
    public static final String DOT_GIT = ".git";
    /** idea目录 */
    public static final String DOT_IDEA = ".idea";

    /** git配置参数 */
    public static final String USER = "user";
    public static final String EMAIL = "email";

    /** java关键字 */
    public static final String PACKAGE = "package";
    public static final String IMPORT = "import";
}
