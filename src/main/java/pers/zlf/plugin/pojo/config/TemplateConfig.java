package pers.zlf.plugin.pojo.config;

import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.TemplateFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/9 14:17
 */
public class TemplateConfig {
    /** 作者 */
    private String author;
    /** 模版 */
    private Map<String,Map<String,String>> totalTemplateMap = new HashMap<>();
    /** 选中的模版 */
    private String selectedTemplate;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, Map<String, String>> getTotalTemplateMap() {
        return totalTemplateMap;
    }

    public void setTotalTemplateMap(Map<String, Map<String, String>> totalTemplateMap) {
        this.totalTemplateMap = totalTemplateMap;
    }

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(String selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

}
