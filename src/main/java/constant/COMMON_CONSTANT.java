package constant;

import util.StringUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:52
 */
public class COMMON_CONSTANT {
    /** 通用常量 */
    public static final String EN = "en";
    public static final String ZH_CN = "zh_cn";
    public static final String BLANK_STRING = "";
    public static final String SUCCESS = "成功";
    public static final String FAIL = "失败";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SPACE = "\\s+";
    public static final String SLASH = "/";
    public static final String PROJECT_INPUT_PLACEHOLDER = "打开多项目时指定项目，单项目不填";
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int,short,long,byte,float,double,boolean、char,Integer,Short,Long,Byte,Float,Double,Boolean,Character".split(","));
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String,Date,Timestamp,BigDecimal".split(","));

    /** FreeMark模板 */
    public static String FULL_PATH = "";
    public static String MODULAR_SHORT_NAME = "";
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";
    public static final String MODEL = "Model";
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "./templates";
    public static final List<String> TEMPLATE_NAME_LIST = Arrays.asList("Model.java.ftl", "Mapper.java.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "VO.java.ftl", "Controller.java.ftl");

    public static void init(String modularName, String packagePath) {
        if (StringUtil.isNotEmpty(modularName)) {
            File file = new File(FULL_PATH + DOUBLE_BACKSLASH + "src");
            if (file.exists()) {
                FULL_PATH = file.getParent();
            }
            FULL_PATH = FULL_PATH + DOUBLE_BACKSLASH + modularName;
            String[] modularNameArr = modularName.split("\\.");
            MODULAR_SHORT_NAME = modularNameArr[modularNameArr.length - 1] + SLASH;
        }
        FULL_PATH = FULL_PATH + JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + DOUBLE_BACKSLASH;
    }
}
