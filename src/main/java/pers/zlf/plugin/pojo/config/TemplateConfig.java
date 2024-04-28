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
    /** 模版（待删除） */
    private Map<String,String> templateMap = new HashMap<>();
    /** 过度字段（待删除） */
    private boolean syncFlag = false;
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

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, String> templateMap) {
        this.templateMap = templateMap;
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

    public boolean isSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    public void initMap() {
        if (!syncFlag) {
            totalTemplateMap.clear();
            Map<String, String> templateValueMap = new HashMap<>();
            Map<String, String> defaultMap = TemplateFactory.getInstance().getAllDefaultTemplate();
            for (Map.Entry<String, String> templateEntry : defaultMap.entrySet()) {
                String key = templateEntry.getKey();
                String title = key.replace(ClassType.FREEMARKER_FILE, Common.BLANK_STRING);
                templateValueMap.put(title, defaultMap.get(key));
            }
            totalTemplateMap.put(Common.DEFAULT_TEMPLATE, templateValueMap);
            if (templateMap != null && !templateMap.isEmpty()){
                totalTemplateMap.put(Common.CUSTOM_TEMPLATE, templateMap);
            }
        }
    }
}
