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
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String SEMICOLON = ";";
    public static final String LEFT_BRACE = "{";
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";

    /** 正则 */
    public static final String SQL_REPLACE_REGEX = "[',`]";
    public static final String SPACE_REGEX = "\\s+";
    public static final String APOSTROPHE_EN_REGEX = "'(.*?)'";
    public static final String DOT_REGEX = "\\.";
    public static final String DOUBLE_QUOTES_REGEX = "\"";
    public static final String WRAP_REGEX = "[\n\r/*]";
    public static final String PARENTHESES_REGEX = "<(.*?)>";

    /** FreeMark模板 */
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "templates";

    /** 数据库 */
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String TABLE = "TABLE";
    public static final String COMMENT = "COMMENT";
    public static final String CREATE = "CREATE";
    public static final String[] SELECT_OPTIONS = {"=", ">", ">=", "<", "<=", "in", "not in", "like", "not like"};

    /** GUI */
    public static final String FULL_PATH_INPUT_PLACEHOLDER = "C:\\workspace\\javaCodeHelper\\src\\main\\java\\javaCodeHelperFile";
    public static final String PACKAGR_PATH_INPUT_PLACEHOLDER = "com.zlf.service.impl";
    public static final String CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER = "C:\\zlf\\CustomTemplate";

    /** 请求方式 */
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";

    /** 注解类型 */
    public static final String FEIGN = "FEIGN";
    public static final String CONTROLLER = "CONTROLLER";

    /** 配置页菜单名 */
    public static final String JAVA_CODE_HELP = "JavaCodeHelp";

    /** 跳转类型 */
    public static final String MODULAR = "modular";
    public static final String GATEWAY = "gateway";
}
