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
    public static final String SUCCESS = "成功";
    public static final String FAIL = "失败";
    public static final String ENCODING = "UTF-8";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SPACE = "\\s+";
    public static final String SLASH = "/";
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";
    public static final String MODEL = "Model";
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "./templates";
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int,short,long,byte,float,double,boolean、char,Integer,Short,Long,Byte,Float,Double,Boolean,Character".split(","));
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String,Date,Timestamp,BigDecimal".split(","));
    public static final List<String> TEMPLATE_NAME_LIST = Arrays.asList("Model.java.ftl", "Mapper.java.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "VO.java.ftl", "Controller.java.ftl");

    public static String PROJECT_PATH = "";
    public static String FULL_PATH = "";
    public static String MODULAR_SHORT_NAME = "";

    public static void init(String modularName, String packagePath){
        String fullPath = PROJECT_PATH;
        if (StringUtil.isNotEmpty(modularName)) {
            File file = new File(PROJECT_PATH + DOUBLE_BACKSLASH + "src");
            if (file.exists()){
                fullPath = file.getParent();
            }
            fullPath = fullPath + DOUBLE_BACKSLASH + modularName;
            String[] modularNameArr = modularName.split("\\.");
            MODULAR_SHORT_NAME = modularNameArr[modularNameArr.length - 1] + SLASH;
        }
        FULL_PATH = fullPath + JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + DOUBLE_BACKSLASH;
    }
}
