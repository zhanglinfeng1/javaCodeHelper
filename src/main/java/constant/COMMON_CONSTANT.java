package constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:52
 */
public class COMMON_CONSTANT {
    /** 通用常量 */
    public static final String SUCCESS = "成功";
    public static final String EN = "en";
    public static final String ZH_CN = "zh_cn";
    public static final String BLANK_STRING = "";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SLASH = "/";
    public static final String COMMA = ",";
    public static final String UNDERSCORE = "_";
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int,short,long,byte,float,double,boolean,char,Integer,Short,Long,Byte,Float,Double,Boolean,Character".split(COMMA));
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String,Date,Timestamp,BigDecimal".split(COMMA));

    /** 正则 */
    public static final String SQL_REPLACE_REGEX = "[',`]";
    public static final String SPACE_REGEX = "\\s+";
    public static final String APOSTROPHE_EN_REGEX = "'(.*?)'";
    public static final String DOT_REGEX = "\\.";

    /** FreeMark模板 */
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "templates";

    /** 数据库 */
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String TABLE = "TABLE";
    public static final String COMMENT = "COMMENT";
    public static final String CREATE = "CREATE";

    /** GUI */
    public static final String PROJECT_INPUT_PLACEHOLDER = "打开多项目时指定项目，单项目不填";
    public static final String PACKAGR_PATH_INPUT_PLACEHOLDER = "例：com.zlf.service.impl";
    public static final String[] SELECT_OPTIONS = {"=", "in", "not in", "like"};
    public static final String[] DATA_BASE_TYPE_OPTIONS = {MYSQL, ORACLE};

}
