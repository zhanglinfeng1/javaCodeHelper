package util;

import constant.COMMON_CONSTANT;
import freemarker.template.TemplateException;
import pojo.TableInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:42
 */

public class CreateFileUtil {

    public void createFile(String author, String modelName, String packagePath, String createTableSql) throws Exception{
        //初始化路径和模板
        COMMON_CONSTANT.init(author, modelName, packagePath);
        //解析Sql
        TableInfo tableInfo = new TableInfo(createTableSql);
        Map<String, Object> dataMap = tableInfo.toMap();
        dataMap.putAll(COMMON_CONSTANT.pathMap);
        //生成文件
        File file = new File(COMMON_CONSTANT.FULL_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        COMMON_CONSTANT.templateList.forEach(t -> {
            try {
                t.process(dataMap, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tableInfo.getFilePath(t.getName())))));
            } catch (TemplateException | IOException e) {
                e.printStackTrace();
            }
        });
    }

}
