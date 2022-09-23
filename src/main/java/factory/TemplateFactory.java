package factory;

import constant.COMMON_CONSTANT;
import freemarker.template.Configuration;
import freemarker.template.Template;
import pojo.ColumnInfo;
import pojo.TableInfo;
import util.DateUtil;
import util.JsonUtil;
import util.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.JarURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    private static Configuration configuration;
    /** 模板文件 */
    private List<Template> templateList;
    /** 解析后的表信息 */
    private TableInfo tableInfo;
    /** 全路径 */
    private static String fullPath;

    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws Exception {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                if (templateFactory == null) {
                    templateFactory = new TemplateFactory();
                    templateFactory.templateList = new ArrayList<>();
                    ClassLoader classLoader = TemplateFactory.class.getClassLoader();
                    configuration = new Configuration(Configuration.VERSION_2_3_23);
                    configuration.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
                    configuration.setClassLoaderForTemplateLoading(classLoader, COMMON_CONSTANT.TEMPLATE_PATH);
                    JarURLConnection jarCon = (JarURLConnection) Objects.requireNonNull(classLoader.getResource(COMMON_CONSTANT.TEMPLATE_PATH)).openConnection();
                    Enumeration<JarEntry> jarEntryArr = jarCon.getJarFile().entries();
                    while (jarEntryArr.hasMoreElements()) {
                        JarEntry entry = jarEntryArr.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(COMMON_CONSTANT.TEMPLATE_PATH) && name.endsWith(COMMON_CONSTANT.TEMPLATE_SUFFIX)) {
                            templateFactory.templateList.add(configuration.getTemplate(name.replace(COMMON_CONSTANT.TEMPLATE_PATH + COMMON_CONSTANT.SLASH, COMMON_CONSTANT.BLANK_STRING)));
                        }
                    }
                }
            }
        }
        return templateFactory;
    }

    public void init(String basicPath, String projectName, String packagePath, TableInfo tableInfo) throws Exception {
        getInstance();
        this.tableInfo = tableInfo;
        this.tableInfo.setDateTime(DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        this.tableInfo.setPackagePath(packagePath);
        if (StringUtil.isNotEmpty(projectName)) {
            File file = new File(basicPath + COMMON_CONSTANT.DOUBLE_BACKSLASH + "src");
            if (file.exists()) {
                basicPath = file.getParent();
            }
            basicPath = basicPath + COMMON_CONSTANT.DOUBLE_BACKSLASH + projectName;
            String[] projectNameArr = projectName.split("[.\\-_]");
            this.tableInfo.setProjectName(projectNameArr[projectNameArr.length - 1] + COMMON_CONSTANT.SLASH);
        }
        fullPath = basicPath + COMMON_CONSTANT.JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + COMMON_CONSTANT.DOUBLE_BACKSLASH;
    }

    public void create(List<ColumnInfo> queryColumnList) throws Exception {
        File file = new File(fullPath);
        if (!file.exists()) {
            if (!file.mkdirs()){
                throw new Exception("创建路径失败");
            }
        }
        tableInfo.setQueryColumnList(queryColumnList);
        for (Template template : templateFactory.templateList) {
            String filePath = fullPath + tableInfo.getTableName() + template.getName().replaceAll(COMMON_CONSTANT.TEMPLATE_SUFFIX, COMMON_CONSTANT.BLANK_STRING);
            try {
                template.process(JsonUtil.toMap(tableInfo), new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath))));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("生成文件失败");
            }
        }
    }

    public void addCustomTemplates(String customTemplatesPath) throws Exception {
        File file = new File(customTemplatesPath);
        if (file.exists() && file.isDirectory() && null != file.listFiles()) {
            configuration.setDirectoryForTemplateLoading(file);
            for (File subFile : file.listFiles()) {
                String name = subFile.getName();
                if (name.endsWith(COMMON_CONSTANT.TEMPLATE_SUFFIX)) {
                    templateFactory.templateList.add(configuration.getTemplate(name));
                }
            }
        }
    }
}
