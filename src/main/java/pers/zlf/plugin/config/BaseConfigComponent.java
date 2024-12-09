package pers.zlf.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/9 13:24
 */
public abstract class BaseConfigComponent<T> implements PersistentStateComponent<T> {
    /** 配置 */
    protected T config;

    @Nullable
    @Override
    public T getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull T config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
