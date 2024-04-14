package pers.zlf.plugin.pojo.config;

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
    private Map<String,String> templateMap = new HashMap<>();

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, String> templateMap) {
        this.templateMap = templateMap;
    }
}
