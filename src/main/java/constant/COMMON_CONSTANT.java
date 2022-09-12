package constant;

import freemarker.template.Configuration;
import freemarker.template.Template;
import util.StringUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static List<Template> templateList;
    public static Map<String,Object> pathMap;

    public static final String SUCCESS = "成功";
    public static final String FAIL = "失败";
    public static final String ENCODING = "UTF-8";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String SLASH = "/";
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";

    public static void init(String author, String modelName, String packagePath) throws IOException {
        pathMap = new HashMap<>();
        pathMap.put("author", author);
        pathMap.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        pathMap.put("packagePath", packagePath);
        String fullPath = PROJECT_PATH;
        if (StringUtil.isNotEmpty(modelName)) {
            fullPath = fullPath + DOUBLE_BACKSLASH + modelName;
            String[] modelNameArr = modelName.split(".");
            modelName = modelNameArr[modelNameArr.length - 1] + SLASH;
        } else {
            modelName = "";
        }
        pathMap.put("modelName", modelName);
        FULL_PATH = fullPath + JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + DOUBLE_BACKSLASH;
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassLoaderForTemplateLoading(COMMON_CONSTANT.class.getClassLoader(), "./templates");
        configuration.setDefaultEncoding(COMMON_CONSTANT.ENCODING);
        templateList = new ArrayList<>();
        templateList.add(configuration.getTemplate("Model.java.ftl"));
        templateList.add(configuration.getTemplate("Mapper.java.ftl"));
        templateList.add(configuration.getTemplate("Service.java.ftl"));
        templateList.add(configuration.getTemplate("ServiceImpl.java.ftl"));
        templateList.add(configuration.getTemplate("VO.java.ftl"));
        templateList.add(configuration.getTemplate("Controller.java.ftl"));
    }
}
