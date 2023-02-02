package pers.zlf.plugin.factory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.JarURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    private static Configuration configuration;
    /** 默认模板文件 */
    private final List<Template> defaultTemplateList = new ArrayList<>();
    /** 解析后的表信息 */
    private TableInfo tableInfo;
    /** 全路径 */
    private String fullPath;

    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws Exception {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                if (templateFactory == null) {
                    templateFactory = new TemplateFactory();
                    //加载默认模板
                    ClassLoader classLoader = TemplateFactory.class.getClassLoader();
                    configuration = new Configuration(Configuration.VERSION_2_3_23);
                    configuration.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
                    configuration.setClassLoaderForTemplateLoading(classLoader, COMMON.TEMPLATE_PATH);
                    JarURLConnection jarCon = (JarURLConnection) Objects.requireNonNull(classLoader.getResource(COMMON.TEMPLATE_PATH)).openConnection();
                    Enumeration<JarEntry> jarEntryArr = jarCon.getJarFile().entries();
                    while (jarEntryArr.hasMoreElements()) {
                        JarEntry entry = jarEntryArr.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(COMMON.TEMPLATE_PATH) && name.endsWith(COMMON.TEMPLATE_SUFFIX)) {
                            templateFactory.defaultTemplateList.add(configuration.getTemplate(name.replace(COMMON.TEMPLATE_PATH + COMMON.SLASH, COMMON.BLANK_STRING)));
                        }
                    }
                }
            }
        }
        return templateFactory;
    }

    public void init(String fullPath, String packagePath, TableInfo tableInfo) throws Exception {
        getInstance();
        this.tableInfo = tableInfo;
        this.tableInfo.setDateTime(DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        this.tableInfo.setPackagePath(packagePath);
        this.fullPath = fullPath + (fullPath.endsWith(COMMON.DOUBLE_BACKSLASH) ? COMMON.BLANK_STRING : COMMON.DOUBLE_BACKSLASH);
    }

    public void create(List<ColumnInfo> queryColumnList, boolean useDefaultTemplate) throws Exception {
        List<Template> templateList = new ArrayList<>();
        if (!useDefaultTemplate) {
            //添加自定义模板
            String customTemplatesPath = Empty.of(ConfigFactory.getInstance().getCommonConfig().getCustomTemplatesPath()).ifEmptyThrow(() -> new Exception("Please configure first! File > Setting > Other Settings > JavaCodeHelp"));
            File file = new File(customTemplatesPath);
            Equals.of(file.exists()).ifFalseThrow(() -> new Exception("Custom template path error"));
            Equals.of(file.isDirectory()).ifFalseThrow(() -> new Exception("Non folder path"));
            configuration.setDirectoryForTemplateLoading(file);
            for (File subFile : Objects.requireNonNull(file.listFiles(), "The custom template does not exist")) {
                String name = subFile.getName();
                if (name.endsWith(COMMON.TEMPLATE_SUFFIX)) {
                    templateList.add(configuration.getTemplate(name));
                }
            }
            Empty.of(templateList).ifEmptyThrow(() -> new Exception("The custom template does not exist"));
        }else {
            templateList = templateFactory.defaultTemplateList;
        }
        Equals.of(new File(this.fullPath)).and(File::exists).or(File::mkdirs).ifFalseThrow(() -> new Exception("Failed to create path"));
        tableInfo.setQueryColumnList(queryColumnList);
        Map<String, Object> map = JsonUtil.toMap(tableInfo);
        for (Template template : templateList) {
            String filePath = this.fullPath + tableInfo.getTableName() + template.getName().replaceAll(COMMON.TEMPLATE_SUFFIX, COMMON.BLANK_STRING);
            try {
                template.process(map, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath))));
            } catch (Exception e) {
                throw new Exception("Failed to generate file");
            }
        }
    }

    public List<Template> getDefaultTemplateList() {
        return defaultTemplateList;
    }
}
