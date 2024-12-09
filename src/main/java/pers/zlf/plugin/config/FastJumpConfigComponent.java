package pers.zlf.plugin.config;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.FastJumpConfig;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
@State(name = Common.FAST_JUMP, storages = @Storage("fastJumpConfig.xml"))
public class FastJumpConfigComponent extends BaseConfigComponent<FastJumpConfig> {
    public FastJumpConfigComponent() {
        config = new FastJumpConfig();
    }
}
