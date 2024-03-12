package pers.zlf.plugin.factory;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import freemarker.template.Configuration;
import freemarker.template.Template;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    /** freemarker版本 */
    private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
    /** 解析后的表信息 */
    private TableInfo tableInfo;
    /** 全路径 */
    private String fullPath;

    private TemplateFactory() {
        configuration.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        configuration.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), Common.TEMPLATE_PATH);
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
     * 初始化基本信息
     *
     * @param fullPath    全路径
     * @param packagePath 包路径
     * @param tableInfo   表信息
     */
    public void init(String fullPath, String packagePath, TableInfo tableInfo) {
        getInstance();
        this.tableInfo = tableInfo;
        this.tableInfo.setPackagePath(packagePath);
        this.fullPath = fullPath + (fullPath.endsWith(Common.DOUBLE_BACKSLASH) ? Common.BLANK_STRING : Common.DOUBLE_BACKSLASH);
    }

    /**
     * 生成文件
     *
     * @param queryColumnList    查询字段
     * @param useDefaultTemplate true：使用默认模版 false：使用自定义模版
     * @throws Exception 异常
     */
    public void create(List<ColumnInfo> queryColumnList, boolean useDefaultTemplate) throws Exception {
        Equals.of(new File(fullPath)).and(File::exists).or(File::mkdirs).ifFalseThrow(() -> new Exception(Message.FULL_PATH_CREATE_ERROR));
        tableInfo.setDateTime(DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        tableInfo.setQueryColumnList(queryColumnList);
        Map<String, Object> map = JsonUtil.toMap(tableInfo);
        if (useDefaultTemplate) {
            configuration.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), Common.TEMPLATE_PATH);
            for (String templateName : Common.TEMPLATE_LIST) {
                create(configuration.getTemplate(templateName), map);
            }
        } else {
            //添加自定义模板
            String customTemplatesPath = Empty.of(ConfigFactory.getInstance().getCommonConfig().getCustomTemplatesPath()).ifEmptyThrow(() -> new Exception(Message.CUSTOMER_TEMPLATE_PATH_CONFIGURATION));
            File file = new File(customTemplatesPath);
            Equals.of(file.exists()).ifFalseThrow(() -> new Exception(Message.CUSTOMER_TEMPLATE_PATH_NOT_EXISTS));
            Equals.of(file.isDirectory()).ifFalseThrow(() -> new Exception(Message.CUSTOMER_TEMPLATE_PATH_NOT_FOLDER));
            configuration.setDirectoryForTemplateLoading(file);
            boolean empty = true;
            for (File subFile : Objects.requireNonNull(file.listFiles(), Message.CUSTOMER_TEMPLATE_PATH_NO_FILE)) {
                String name = subFile.getName();
                if (name.endsWith(ClassType.FREEMARKER_FILE)) {
                    create(configuration.getTemplate(name), map);
                    empty = false;
                }
            }
            Equals.of(empty).ifTrueThrow(() -> new Exception(Message.CUSTOMER_TEMPLATE_PATH_NO_FILE));
        }
    }

    /**
     * 生成文件
     *
     * @param template 模版
     * @param map      模版数据
     * @throws Exception 异常
     */
    private void create(Template template, Map<String, Object> map) throws Exception {
        String filePath = fullPath + tableInfo.getTableName() + template.getName().replaceAll(ClassType.FREEMARKER_FILE, Common.BLANK_STRING);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            template.process(map, bufferedWriter);
        } catch (Exception e) {
            throw new Exception(filePath + Message.CREATE_FILE_ERROR + e.getMessage());
        }
    }

    /**
     * 下载默认模版
     *
     * @return boolean
     * @throws IOException 异常
     */
    public boolean download() throws IOException {
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
        if (null != virtualFile) {
            for (String templateName : Common.TEMPLATE_LIST) {
                Template template = configuration.getTemplate(templateName);
                FileWriter file = new FileWriter(virtualFile.getPath() + Common.DOUBLE_BACKSLASH + template.getName(), true);
                //TODO 寻找替换方法
                file.append(template.getRootTreeNode().toString());
                file.flush();
                file.close();
            }
            return true;
        }
        return false;
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
            Template template = configuration.getTemplate(templateName);
            template.process(map, stringWriter);
        } catch (Exception ignored) {
        }
        return Arrays.stream(stringWriter.toString().split(Regex.WRAP)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(Common.WRAP));
    }
}
