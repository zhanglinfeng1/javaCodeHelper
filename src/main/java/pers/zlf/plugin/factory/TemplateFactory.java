package pers.zlf.plugin.factory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.lambda.Equals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    /** freemarker版本 */
    private final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_23);
    private final String TEMPLATE_PATH = "templates";
    private final List<String> TEMPLATE_LIST = List.of("Model.java.ftl", "VO.java.ftl", "Mapper.java.ftl", "Mapper.xml.ftl", "Service.java.ftl", "ServiceImpl.java.ftl", "Controller.java.ftl");

    private TemplateFactory() {
        CONFIGURATION.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        CONFIGURATION.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), TEMPLATE_PATH);
    }

    public static TemplateFactory getInstance() {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                Equals.of(templateFactory == null).ifTrue(() -> templateFactory = new TemplateFactory());
            }
        }
        return templateFactory;
    }

    /**
     * 生成文件
     *
     * @param selectedTemplate    选择的模版
     * @param selectedTemplateMap key:文件名 value:模版名
     * @param filePath            文件路径
     * @param tableInfo           表信息
     * @throws Exception 异常
     */
    public void create(String selectedTemplate, Map<String, String> selectedTemplateMap, String filePath, TableInfo tableInfo) throws Exception {
        Equals.of(new File(filePath)).and(File::exists).or(File::mkdirs).ifFalseThrow(() -> new Exception(Message.FULL_PATH_CREATE_ERROR));
        tableInfo.setDateTime(DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        Map<String, Object> map = JsonUtil.toMap(tableInfo);
        //创建临时模版文件
        String temporaryFilePath = Path.of(filePath, Common.JAVA_CODE_HELPER).toString();
        File file = new File(temporaryFilePath);
        try {
            createTemporaryFile(selectedTemplate, temporaryFilePath);
            //添加自定义模板
            CONFIGURATION.setDirectoryForTemplateLoading(file);
            for (Map.Entry<String, String> entry : selectedTemplateMap.entrySet()) {
                String fileName = entry.getKey();
                String templateFileName = entry.getValue() + FileType.FREEMARKER_FILE;
                create(filePath, fileName, CONFIGURATION.getTemplate(templateFileName), map);
            }
        } finally {
            //删除临时模版文件
            Optional.ofNullable(file.listFiles()).ifPresent(t -> Arrays.stream(t).forEach(File::delete));
            file.delete();
        }
    }

    /**
     * 生成临时模版文件
     *
     * @param selectedTemplate 选择的模版
     * @throws IOException 异常
     */
    private void createTemporaryFile(String selectedTemplate, String filePath) throws Exception {
        Equals.of(new File(filePath)).and(File::exists).or(File::mkdirs);
        Map<String, String> templateMap = ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap().get(selectedTemplate);
        for (Map.Entry<String, String> templateEntry : templateMap.entrySet()) {
            FileWriter file = new FileWriter(Path.of(filePath, templateEntry.getKey() + FileType.FREEMARKER_FILE).toString(), true);
            file.append(templateEntry.getValue());
            file.flush();
            file.close();
        }
    }

    /**
     * 生成文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param template 模版
     * @param map      模版数据
     * @throws Exception 异常
     */
    private void create(String filePath, String fileName, Template template, Map<String, Object> map) throws Exception {
        filePath = Path.of(filePath, fileName).toString();
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            template.process(map, bufferedWriter);
        } catch (Exception e) {
            throw new Exception(filePath + Message.CREATE_FILE_ERROR + e.getMessage());
        }
    }

    /**
     * 获取模版渲染后的内容
     *
     * @param templateName 模版名称
     * @param map          模版数据
     * @return String
     */
    public String getTemplateContent(String templateName, Map<String, ?> map) {
        // 接收处理后的模版内容
        StringWriter stringWriter = new StringWriter();
        try {
            Template template = CONFIGURATION.getTemplate(templateName);
            template.process(map, stringWriter);
        } catch (Exception ignored) {
        }
        return stringWriter.toString().replaceAll(Regex.WRAP2, Common.BLANK_STRING);
    }

    /**
     * 获取全部默认模版
     *
     * @return Map<String, String>
     */
    public Map<String, String> getAllDefaultTemplate() {
        Map<String, String> map = new HashMap<>();
        for (String templateName : TEMPLATE_LIST) {
            Template template = null;
            try {
                template = CONFIGURATION.getTemplate(templateName);
            } catch (IOException ignored) {
            }
            //TODO 替换getRootTreeNode方法
            map.put(templateName, template.getRootTreeNode().toString());
        }
        return map;
    }
}
