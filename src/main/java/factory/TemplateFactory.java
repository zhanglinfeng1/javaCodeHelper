package factory;

import constant.COMMON_CONSTANT;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/13 17:01
 */
public class TemplateFactory {
    private static volatile TemplateFactory templateFactory;

    private List<Template> templateList = new ArrayList<>();

    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws IOException {
        if (templateFactory == null) {
            synchronized (TemplateFactory.class) {
                if (templateFactory == null) {
                    templateFactory = new TemplateFactory();
                    File file = new File(COMMON_CONSTANT.FULL_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
                    configuration.setClassLoaderForTemplateLoading(COMMON_CONSTANT.class.getClassLoader(), COMMON_CONSTANT.TEMPLATE_PATH);
                    configuration.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
                    for(String templateName : COMMON_CONSTANT.TEMPLATE_NAME_LIST){
                        templateFactory.templateList.add(configuration.getTemplate(templateName));
                    }
                }
            }
        }
        return templateFactory;
    }

    public void create(Object dataModel,String fileBasicName) throws IOException, TemplateException {
        for(Template template : templateFactory.templateList){
            String filePath = COMMON_CONSTANT.FULL_PATH + fileBasicName + template.getName().replaceAll(COMMON_CONSTANT.TEMPLATE_SUFFIX,"").replaceAll(COMMON_CONSTANT.MODEL,"");
            template.process(dataModel, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath))));
        }
    }
}
