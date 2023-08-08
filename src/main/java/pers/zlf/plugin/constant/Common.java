package pers.zlf.plugin.constant;

import java.util.List;

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
    public static final String RIGHT_BRACE = "}";
    public static final String LEFT_BRACKETS = "[";
    public static final String LEFT_PARENTHESES = "(";
    public static final String RIGHT_PARENTHESES = ")";
    public static final String LESS_THAN_SIGN = "<";
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";
    public static final String DOUBLE_QUOTATION = "\"";
    public static final String PERCENT_SIGN = "%";
    public static final String EN = "en";
    public static final String ZH = "zh";
    public static final String WRAP = "\n";
    public static final String HASH = "#";

    /** FreeMark模板 */
    public static final String TEMPLATE_PATH = "templates";
    public static final String JUMP_TO_METHOD_TEMPLATE = "JumpToMethod.ftl";
    public static final String JUMP_TO_XML_TEMPLATE = "JumpToXml.ftl";
    public static final List<String> TEMPLATE_LIST = List.of(".java.ftl", "Controller.java.ftl", "Mapper.java.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "VO.java.ftl");

    /** 占位符 */
    public static final String FULL_PATH_INPUT_PLACEHOLDER = "C:\\workspace\\javaCodeHelper\\src\\main\\java\\javaCodeHelperFile";
    public static final String PACKAGR_PATH_INPUT_PLACEHOLDER = "com.zlf.service.impl";
    public static final String CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER = "e.g. C:\\zlf\\CustomTemplate";

    /** 配置相关 */
    public static final String JAVA_CODE_HELP = "JavaCodeHelp";
    public static final String FAST_JUMP = "快捷跳转";
    public static final String CODE_STATISTICS = "代码统计";
    public static final String SELECT_MODULE = "选择模块";
    /** java时间类型 */
    public static final Integer DATE_CLASS_TYPE = 0;
    /** 表头 */
    public static final String FILE_TYPE_TABLE_HEADER = "参与统计的文件类型(文件后缀，例如：.java)";
    public static final String GIT_EMAIL_TABLE_HEADER = "参与统计贡献率的git邮箱(默认当前邮箱)";
    public static final String[] QUERY_COLUMN_TABLE_HEADER = {"字段名", "别名", "查询方式"};
    /** 查询方式下拉选项 */
    public static final String[] SELECT_OPTIONS = {"=", ">", ">=", "<", "<=", "in", "not in", "like", "not like"};
    /** 翻译 */
    public static final Integer BAIDU_TRANSLATE = 0;
    public static final String BAIDU_TRANSLATE_CHINESE = "百度翻译";
    public static final String BAIDU_TRANSLATE_URL = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&salt=%s&sign=%s&appid=%s";
    /** 接口文档Api */
    public static final Integer SWAGGER_API = 0;

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
    public static final String CONSTRUCTOR_FILL_STR1 = "this.%s=%s;";
    public static final String CONSTRUCTOR_FILL_STR2 = "this.%s=%s.get%s();";

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
    public static final String DOT_IDEA = ".idea";

    /** git配置参数 */
    public static final String USER = "user";
    public static final String EMAIL = "email";

}
