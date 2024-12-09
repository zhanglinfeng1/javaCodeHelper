package pers.zlf.plugin.config;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.TemplateConfig;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/8 11:42
 */
@State(name = Common.TEMPLATE_CONFIG, storages = @Storage("templateConfig.xml"))
public class TemplateConfigComponent extends BaseConfigComponent<TemplateConfig> {
    public TemplateConfigComponent() {
        config = new TemplateConfig();
    }
}
