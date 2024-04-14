package pers.zlf.plugin.factory;

import com.intellij.openapi.application.ApplicationManager;
import pers.zlf.plugin.config.CodeStatisticsConfigComponent;
import pers.zlf.plugin.config.CommonConfigComponent;
import pers.zlf.plugin.config.FastJumpConfigComponent;
import pers.zlf.plugin.config.TemplateConfigComponent;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.pojo.config.FastJumpConfig;
import pers.zlf.plugin.pojo.config.TemplateConfig;
import pers.zlf.plugin.util.lambda.Equals;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/9 10:33
 */
public class ConfigFactory {
    private static volatile ConfigFactory configFactory;
    private CommonConfig commonConfig;
    private FastJumpConfig fastJumpConfig;
    private CodeStatisticsConfig codeStatisticsConfig;
    private TemplateConfig templateConfig;

    private ConfigFactory() {
        commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();
        fastJumpConfig = ApplicationManager.getApplication().getService(FastJumpConfigComponent.class).getState();
        codeStatisticsConfig = ApplicationManager.getApplication().getService(CodeStatisticsConfigComponent.class).getState();
        templateConfig = ApplicationManager.getApplication().getService(TemplateConfigComponent.class).getState();
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

    public FastJumpConfig getFastJumpConfig() {
        return fastJumpConfig;
    }

    public void setFastJumpConfig(FastJumpConfig fastJumpConfig) {
        this.fastJumpConfig = fastJumpConfig;
    }

    public CodeStatisticsConfig getCodeStatisticsConfig() {
        return codeStatisticsConfig;
    }

    public void setCodeStatisticsConfig(CodeStatisticsConfig codeStatisticsConfig) {
        this.codeStatisticsConfig = codeStatisticsConfig;
    }

    public TemplateConfig getTemplateConfig() {
        return templateConfig;
    }

    public void setTemplateConfig(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }
}
