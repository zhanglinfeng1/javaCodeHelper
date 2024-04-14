package pers.zlf.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.TemplateConfig;

import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/8 11:42
 */
@State(name = Common.TEMPLATE_CONFIG, storages = @Storage("templateConfig.xml"))
public class TemplateConfigComponent implements PersistentStateComponent<TemplateConfig> {

    private final TemplateConfig config = new TemplateConfig();

    @Nullable
    @Override
    public TemplateConfig getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull TemplateConfig config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
