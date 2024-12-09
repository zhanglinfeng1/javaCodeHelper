package pers.zlf.plugin.config;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.CommonConfig;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/4 8:18
 */
@State(name = Common.JAVA_CODE_HELPER, storages = @Storage("javaCodeHelpConfig.xml"))
public class CommonConfigComponent extends BaseConfigComponent<CommonConfig> {
    public CommonConfigComponent() {
        config = new CommonConfig();
    }
}
