package pers.zlf.plugin.factory;

import com.intellij.openapi.components.ServiceManager;
import pers.zlf.plugin.config.ConfigComponent;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.lambda.Equals;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/9 10:33
 */
public class ConfigFactory {
    private static volatile ConfigFactory configFactory;
    private CommonConfig commonConfig;

    private ConfigFactory() {
        //TODO 兼容老版本2019.2.4，后续使用ApplicationManager.getApplication().getService(ConfigComponent.class);
        commonConfig = ServiceManager.getService(ConfigComponent.class).getState();
    }

    public static ConfigFactory getInstance() {
        if (configFactory == null) {
            synchronized (ConfigFactory.class) {
                Equals.of(configFactory == null).ifTrue(() -> configFactory = new ConfigFactory());
            }
        }
        return configFactory;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }
}
