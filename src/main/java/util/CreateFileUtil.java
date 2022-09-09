package util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import pojo.TableInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:42
 */
public class CreateFileUtil {

    public void createFile(String createTableSql) throws IOException, TemplateException {
        //解析Sql
        TableInfo tableInfo = new TableInfo(createTableSql);
        Map<String, Object> dataMap = tableInfo.toMap();
        //获取模板
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDirectoryForTemplateLoading(new File("C:\\workspace\\reverseEngineering\\src\\main\\resources\\templates"));
        configuration.setDefaultEncoding("UTF-8");
        //生成文件
        Template template = configuration.getTemplate("Model.java.ftl");
        File docFile = new File(tableInfo.getModelPath());
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
        template.process(dataMap, out);
    }

}
