package constant;

import freemarker.template.Configuration;
import freemarker.template.Template;
import util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:52
 */
public class COMMON_CONSTANT {
    public static String AUTHOR = "";
    public static String PROJECT_PATH = "";
    public static String PACKAGE_PATH = "";
    public static String FULL_PATH = "";
    public static List<Template> templateList;

    public static final String SUCCESS = "成功";
    public static final String FAIL = "失败";
    public static final String ENCODING = "UTF-8";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String JAVA_FILE_PATH = "\\src\\main\\java\\";

    public static void init(String author,String modelName, String packagePath) throws IOException {
        AUTHOR = author;
        PACKAGE_PATH = packagePath;
        String fullPath = PROJECT_PATH;
        if (StringUtil.isNotEmpty(modelName)) {
            fullPath = fullPath + DOUBLE_BACKSLASH + modelName;
        }
        FULL_PATH = fullPath + JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + DOUBLE_BACKSLASH;
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassLoaderForTemplateLoading(COMMON_CONSTANT.class.getClassLoader(), "./templates");
        configuration.setDefaultEncoding(COMMON_CONSTANT.ENCODING);
        templateList = new ArrayList<>();
        templateList.add(configuration.getTemplate("Model.java.ftl"));
    }
}
