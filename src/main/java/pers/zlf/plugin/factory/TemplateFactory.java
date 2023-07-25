package pers.zlf.plugin.factory;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import freemarker.template.Configuration;
import freemarker.template.Template;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;
    /** freemarker版本 */
    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_23);
    /** 解析后的表信息 */
    private TableInfo tableInfo;
    /** 全路径 */
    private String fullPath;

    private TemplateFactory() {
        CONFIGURATION.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        CONFIGURATION.setClassLoaderForTemplateLoading(TemplateFactory.class.getClassLoader(), Common.TEMPLATE_PATH);
    }

    public static TemplateFactory getInstance() {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                Equals.of(templateFactory == null).ifTrue(() -> templateFactory = new TemplateFactory());
            }
        }
        return templateFactory;
    }

    public void init(String fullPath, String packagePath, TableInfo tableInfo) {
        getInstance();
        this.tableInfo = tableInfo;
        this.tableInfo.setPackagePath(packagePath);
        this.fullPath = fullPath + (fullPath.endsWith(Common.DOUBLE_BACKSLASH) ? Common.BLANK_STRING : Common.DOUBLE_BACKSLASH);
    }

    public void create(List<ColumnInfo> queryColumnList, boolean useDefaultTemplate) throws Exception {
        List<Template> templateList = new ArrayList<>();
        if (!useDefaultTemplate) {
            //添加自定义模板
            String customTemplatesPath = Empty.of(ConfigFactory.getInstance().getCommonConfig().getCustomTemplatesPath()).ifEmptyThrow(() -> new Exception("Please configure first! File > Setting > Other Settings > JavaCodeHelp"));
            File file = new File(customTemplatesPath);
            Equals.of(file.exists()).ifFalseThrow(() -> new Exception("Custom template path error"));
            Equals.of(file.isDirectory()).ifFalseThrow(() -> new Exception("Non folder path"));
            CONFIGURATION.setDirectoryForTemplateLoading(file);
            for (File subFile : Objects.requireNonNull(file.listFiles(), "The custom template does not exist")) {
                String name = subFile.getName();
                if (name.endsWith(ClassType.FREEMARKER_FILE)) {
                    templateList.add(CONFIGURATION.getTemplate(name));
                }
            }
            Empty.of(templateList).ifEmptyThrow(() -> new Exception("The custom template does not exist"));
        } else {
            for (String templateName : Common.TEMPLATE_LIST) {
                templateList.add(CONFIGURATION.getTemplate(templateName));
            }
        }
        Equals.of(new File(this.fullPath)).and(File::exists).or(File::mkdirs).ifFalseThrow(() -> new Exception("Failed to create path"));
        tableInfo.setDateTime(DateUtil.nowStr(DateUtil.YYYY_MM_DDHHMMSS));
        tableInfo.setQueryColumnList(queryColumnList);
        Map<String, Object> map = JsonUtil.toMap(tableInfo);
        for (Template template : templateList) {
            String filePath = this.fullPath + tableInfo.getTableName() + template.getName().replaceAll(ClassType.FREEMARKER_FILE, Common.BLANK_STRING);
            try {
                template.process(map, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath))));
            } catch (Exception e) {
                throw new Exception("Failed to generate file");
            }
        }
    }

    public void download() throws IOException {
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
        if (null != virtualFile) {
            for (String templateName : Common.TEMPLATE_LIST) {
                Template template = CONFIGURATION.getTemplate(templateName);
                FileWriter file = new FileWriter(virtualFile.getPath() + Common.DOUBLE_BACKSLASH + template.getName(), true);
                //TODO 寻找替换方法
                file.append(template.getRootTreeNode().toString());
                file.flush();
                file.close();
            }
        }
    }
}
