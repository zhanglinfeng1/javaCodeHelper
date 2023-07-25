package pers.zlf.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.CommonConfig;

import java.util.Objects;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/4 8:18
 */
@State(name = Common.JAVA_CODE_HELP, storages = @Storage("javaCodeHelpConfig.xml"))
public class CommonConfigComponent implements PersistentStateComponent<CommonConfig> {

    private final CommonConfig config = new CommonConfig();

    @Nullable
    @Override
    public CommonConfig getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull CommonConfig config) {
        XmlSerializerUtil.copyBean(config, Objects.requireNonNull(getState()));
    }
}
