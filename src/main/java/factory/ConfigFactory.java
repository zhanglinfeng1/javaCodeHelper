package factory;

import com.intellij.openapi.components.ServiceManager;
import component.ConfigComponent;
import pojo.CommonConfig;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/9 10:33
 */
public class ConfigFactory {
    private static volatile ConfigFactory configFactory;
    private CommonConfig commonConfig;

    public static ConfigFactory getInstance() {
        if (configFactory == null) {
            synchronized (ConfigFactory.class) {
                if (configFactory == null) {
                    configFactory = new ConfigFactory();
                    configFactory.commonConfig = ServiceManager.getService(ConfigComponent.class).getState();
                }
            }
        }
        return configFactory;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void updateCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }
}