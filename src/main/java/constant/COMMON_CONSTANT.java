package constant;

import util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:52
 */
public class COMMON_CONSTANT {
    public static String PROJECT_PATH = "";
    public static String FULL_PATH = "";
    public static Map<String,Object> pathMap;

    public static final String SUCCESS = "成功";
    public static final String FAIL = "失败";
    public static final String ENCODING = "UTF-8";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SLASH = "/";
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";
    public static final String MODEL = "Model";
    public static final String TEMPLATE_SUFFIX = ".ftl";
    public static final String TEMPLATE_PATH = "./templates";
    public static final List<String> TEMPLATE_NAME_LIST = Arrays.asList("Model.java.ftl", "Mapper.java.ftl", "Service.java.ftl", "ServiceImpl.java.ftl",
            "VO.java.ftl", "Controller.java.ftl");

    public static void init(String author, String modelName, String packagePath){
        pathMap = new HashMap<>();
        pathMap.put("author", author);
        pathMap.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        pathMap.put("packagePath", packagePath);
        String fullPath = PROJECT_PATH;
        if (StringUtil.isNotEmpty(modelName)) {
            fullPath = fullPath + DOUBLE_BACKSLASH + modelName;
            String[] modelNameArr = modelName.split("\\.");
            modelName = modelNameArr[modelNameArr.length - 1] + SLASH;
        } else {
            modelName = "";
        }
        pathMap.put("modelName", modelName);
        FULL_PATH = fullPath + JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + DOUBLE_BACKSLASH;
    }
}
