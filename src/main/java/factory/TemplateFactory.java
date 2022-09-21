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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    /** 模板文件 */
    private List<Template> templateList = new ArrayList<>();
    /** 解析后的表信息 */
    private static TableInfo tableInfo;
    /** 解析后的表信息 */
    private static Map<String, Object> dataMap;
    /** 全路径 */
    private static String fullPath;

    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws Exception {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                if (templateFactory == null) {
                    templateFactory = new TemplateFactory();
                    Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
                    configuration.setClassLoaderForTemplateLoading(COMMON_CONSTANT.class.getClassLoader(), COMMON_CONSTANT.TEMPLATE_PATH);
                    configuration.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
                    for (String templateName : COMMON_CONSTANT.TEMPLATE_NAME_LIST) {
                        try {
                            templateFactory.templateList.add(configuration.getTemplate(templateName));
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new Exception("模板获取失败");
                        }
                    }
                }
            }
        }
        return templateFactory;
    }

    public static void init(String basicPath, String author, String projectName, String packagePath, String createTableSql) throws Exception {
        getInstance();
        try {
            tableInfo = new TableInfo(createTableSql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("检查建表SQL");
        }
        dataMap = new HashMap<>();
        dataMap.put("author", author);
        dataMap.put("dateTime", DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        dataMap.put("packagePath", packagePath);
        fullPath = basicPath;
        if (StringUtil.isNotEmpty(projectName)) {
            File file = new File(basicPath + COMMON_CONSTANT.DOUBLE_BACKSLASH + "src");
            if (file.exists()) {
                fullPath = file.getParent();
            }
            fullPath = fullPath + COMMON_CONSTANT.DOUBLE_BACKSLASH + projectName;
            String[] projectNameArr = projectName.split("\\.");
            dataMap.put("projectName", projectNameArr[projectNameArr.length - 1] + COMMON_CONSTANT.SLASH);
        }
        fullPath = fullPath + COMMON_CONSTANT.JAVA_FILE_PATH + packagePath.replaceAll("\\.", "\\\\") + COMMON_CONSTANT.DOUBLE_BACKSLASH;
    }

    public static void create(List<ColumnInfo> queryColumnList) throws Exception {
        File file = new File(fullPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        tableInfo.setQueryColumnList(queryColumnList);
        dataMap.putAll(JsonUtil.toMap(tableInfo));
        for (Template template : templateFactory.templateList) {
            String filePath = fullPath + dataMap.get("tableName") + template.getName().replaceAll(COMMON_CONSTANT.TEMPLATE_SUFFIX, COMMON_CONSTANT.BLANK_STRING).replaceAll(COMMON_CONSTANT.MODEL, COMMON_CONSTANT.BLANK_STRING);
            try {
                template.process(dataMap, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath))));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("生成文件失败");
            }
        }
    }

    public static TableInfo getTableInfo() {
        return tableInfo;
    }
}
