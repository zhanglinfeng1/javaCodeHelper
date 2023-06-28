package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CommonConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:10
 */
public class CommonConfigurable implements Configurable {
    /** 配置参数 */
    private final CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
    /** 配置界面 */
    private final CommonConfigDialog dialog = new CommonConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Common.JAVA_CODE_HELP;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getTranslateApi().equals(commonConfig.getTranslateApi())) {
            return true;
        }
        if (!dialog.getAppId().equals(commonConfig.getAppId())) {
            return true;
        }
        if (!dialog.getSecurityKey().equals(commonConfig.getSecretKey())) {
            return true;
        }
        if (!dialog.getDateClassType().equals(commonConfig.getDateClassType())) {
            return true;
        }
        if (!dialog.getApiTool().equals(commonConfig.getApiTool())) {
            return true;
        }
        return !dialog.getCustomTemplatesPath().equals(commonConfig.getCustomTemplatesPath());
    }

    @Override
    public void apply() {
        commonConfig.setTranslateApi(dialog.getTranslateApi());
        commonConfig.setAppId(dialog.getAppId());
        commonConfig.setSecretKey(dialog.getSecurityKey());
        commonConfig.setCustomTemplatesPath(dialog.getCustomTemplatesPath());
        commonConfig.setDateClassType(dialog.getDateClassType());
        commonConfig.setApiTool(dialog.getApiTool());
        ConfigFactory.getInstance().setCommonConfig(commonConfig);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}