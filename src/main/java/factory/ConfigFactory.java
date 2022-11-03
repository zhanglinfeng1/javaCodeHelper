package factory;

import com.intellij.openapi.components.ServiceManager;
import config.ConfigComponent;
import pojo.CommonConfig;

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
                    //TODO 寻找替换方法
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
