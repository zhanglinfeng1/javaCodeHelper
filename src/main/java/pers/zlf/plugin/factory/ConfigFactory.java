package pers.zlf.plugin.factory;

import com.intellij.openapi.components.ServiceManager;
import pers.zlf.plugin.config.ConfigComponent;
import pers.zlf.plugin.pojo.CommonConfig;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/9 10:33
 */
public class ConfigFactory {
    private static volatile ConfigFactory configFactory;
    private CommonConfig commonConfig;

    private ConfigFactory(){

    }

    public static ConfigFactory getInstance() {
        if (configFactory == null) {
            synchronized (ConfigFactory.class) {
                if (configFactory == null) {
                    configFactory = new ConfigFactory();
                    //TODO 兼容老版本，后续使用ApplicationManager.getApplication().getService(ConfigComponent.class);
                    configFactory.commonConfig = ServiceManager.getService(ConfigComponent.class).getState();
                }
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