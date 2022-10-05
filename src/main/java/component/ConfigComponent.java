package component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pojo.Config;

import java.util.Objects;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 8:18
 */
@State(name = "CommonConfig", storages = {@Storage("javaCodeHelpConfig.xml")})
public class ConfigComponent implements PersistentStateComponent<Config> {

    private Config config;

    @Nullable
    @Override
    public Config getState() {
        return config == null ? new Config() : config;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
