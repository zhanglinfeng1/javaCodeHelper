package pers.zlf.plugin.pojo.config;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.factory.TemplateFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        totalTemplateMap = Optional.ofNullable(totalTemplateMap).orElse(new HashMap<>());
        if (!totalTemplateMap.containsKey(Common.DEFAULT_TEMPLATE)) {
            Map<String, String> templateValueMap = new HashMap<>();
            Map<String, String> defaultMap = TemplateFactory.getInstance().getAllDefaultTemplate();
            for (Map.Entry<String, String> templateEntry : defaultMap.entrySet()) {
                String key = templateEntry.getKey();
                String title = key.replace(FileType.FREEMARKER_FILE, Common.BLANK_STRING);
                templateValueMap.put(title, defaultMap.get(key));
            }
            totalTemplateMap.put(Common.DEFAULT_TEMPLATE, templateValueMap);
        }
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
