package pers.zlf.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;

import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
@State(name = Common.CODE_STATISTICS, storages = @Storage(Common.CODE_STATISTICS_CONFIG_XML))
public class CodeStatisticsConfigComponent implements PersistentStateComponent<CodeStatisticsConfig> {

    private final CodeStatisticsConfig config = new CodeStatisticsConfig();

    @Nullable
    @Override
    public CodeStatisticsConfig getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull CodeStatisticsConfig config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
