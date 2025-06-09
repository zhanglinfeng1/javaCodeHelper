package pers.zlf.plugin.config;

import pers.zlf.plugin.dialog.TemplateConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.TemplateConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MapUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Map;
import java.util.Set;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/8 11:42
 */
public class TemplateConfigurable extends BaseConfigurable<TemplateConfigDialog> {
    /** 配置参数 */
    private final TemplateConfig CONFIG = ConfigFactory.getInstance().getTemplateConfig();

    public TemplateConfigurable() {
        dialog = new TemplateConfigDialog();
    }

    @Override
    public boolean isModified() {
        if (!StringUtil.equals(dialog.getAuthor(), CONFIG.getAuthor())) {
            return true;
        }
        Map<String, Map<String, String>> dialogTemplateMap = dialog.getTotalTemplateMap();
        Map<String, Map<String, String>> configTemplateMap = CONFIG.getTotalTemplateMap();
        Set<String> dialogKeySet = dialogTemplateMap.keySet();
        Set<String> configKeySet = configTemplateMap.keySet();
        if (!CollectionUtil.equals(dialogKeySet, configKeySet)) {
            return true;
        }
        for (String key : dialogKeySet) {
            Map<String, String> dialogValue = dialogTemplateMap.get(key);
            Map<String, String> configValue = configTemplateMap.get(key);
            if (!MapUtil.equals(dialogValue, configValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() {
        CONFIG.setAuthor(dialog.getAuthor());
        CONFIG.setTotalTemplateMap(dialog.getTotalTemplateMap());
        CONFIG.setSelectedTemplate(dialog.getSelectedTemplate());
        ConfigFactory.getInstance().setTemplateConfig(CONFIG);
    }

}