package util;

import constant.COMMON_CONSTANT;
import factory.TemplateFactory;
import freemarker.template.TemplateException;
import pojo.TableInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:42
 */

public class CreateFileUtil {

    public void createFile(String author, String modularName, String packagePath, String createTableSql) throws IOException, TemplateException {
        //初始化路径
        COMMON_CONSTANT.init(modularName, packagePath);
        //解析Sql
        TableInfo tableInfo = new TableInfo(createTableSql);
        Map<String, Object> dataMap = tableInfo.toMap();
        dataMap.put("author", author);
        dataMap.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        dataMap.put("packagePath", packagePath);
        dataMap.put("modelName", COMMON_CONSTANT.MODULAR_SHORT_NAME);
        //生成文件
        TemplateFactory templateFactory = TemplateFactory.getInstance();
        templateFactory.create(dataMap,tableInfo.getTableName());
    }
}
