package pers.zlf.plugin.config;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
@State(name = Common.CODE_STATISTICS, storages = @Storage("codeStatisticsConfig.xml"))
public class CodeStatisticsConfigComponent extends BaseConfigComponent<CodeStatisticsConfig> {

    public CodeStatisticsConfigComponent() {
        config = new CodeStatisticsConfig();
    }

}
