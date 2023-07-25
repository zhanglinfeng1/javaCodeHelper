package pers.zlf.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.FastJumpConfig;

import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
@State(name = Common.FAST_JUMP, storages = @Storage("fastJumpConfig.xml"))
public class FastJumpConfigComponent implements PersistentStateComponent<FastJumpConfig> {

    private final FastJumpConfig config = new FastJumpConfig();

    @Nullable
    @Override
    public FastJumpConfig getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull FastJumpConfig config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
