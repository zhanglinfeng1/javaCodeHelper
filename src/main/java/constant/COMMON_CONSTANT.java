package constant;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:52
 */
public class COMMON_CONSTANT {
    /** 通用常量 */
    public static final String BAIDU_TRANSLATE = "百度翻译";
    public static final String SUCCESS = "成功";
    public static final String EN = "en";
    public static final String ZH = "zh";
    public static final int SOCKET_TIMEOUT = 2000;
    public static final String BLANK_STRING = "";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String SEMICOLON = ";";
    public static final String LEFT_BRACE = "{";
    public static final String LEFT_BRACKETS = "[";
    public static final String RIGHT_PARENTHESES = ")";
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";
    public static final String EQUAL_SIGN = "=";

    /** 正则 */
    public static final String SQL_REPLACE_REGEX = "[',`]";
    public static final String SPACE_REGEX = "\\s+";
    public static final String APOSTROPHE_EN_REGEX = "'(.*?)'";
    public static final String DOT_REGEX = "\\.";
    public static final String DOUBLE_QUOTES_REGEX = "\"";
    public static final String WRAP_REGEX = "[\n\r/*]";
    public static final String PARENTHESES_REGEX = "<(.*?)>";
    public static final String LEFT_BRACKETS_REGEX = "\\[";

    /** FreeMark模板 */
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "templates";

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

    /** 请求方式 */
    public static final String GET = "GET";

    /** 配置页菜单名 */
    public static final String JAVA_CODE_HELP = "JavaCodeHelp";

    /** 跳转类型 */
    public static final String MODULAR = "modular";
    public static final String GATEWAY = "gateway";

    /** 用于拼接的字符串 */
    public static final String THIS_STR = "this.";
    public static final String EQ_STR = " = ";
    public static final String GET_STR = ".get";
    public static final String END_STR = "();";
    public static final String COMMA_STR = ", ";
    public static final String TO_STR = " to ";
    public static final String STREAM_STR = ".stream()";
    public static final String MAP_STR = ".map(";

    public static final String COLLECT_LIST_STR = "::new).collect(Collectors.toList());";
    public static final String ARRAYS_STREAM = "Arrays.stream(";

}
