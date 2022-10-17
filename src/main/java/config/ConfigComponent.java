package config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import constant.COMMON_CONSTANT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pojo.CommonConfig;

import java.util.Objects;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/4 8:18
 */
@State(name = COMMON_CONSTANT.JAVA_CODE_HELP, storages = {@Storage("javaCodeHelpConfig.xml")})
public class ConfigComponent implements PersistentStateComponent<CommonConfig> {

    private final CommonConfig commonConfig = new CommonConfig();

    @Nullable
    @Override
    public CommonConfig getState() {
        return commonConfig;
    }

    @Override
    public void loadState(@NotNull CommonConfig commonConfig) {
        XmlSerializerUtil.copyBean(commonConfig, Objects.requireNonNull(getState()));
    }
}
