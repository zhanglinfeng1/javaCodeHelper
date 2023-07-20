package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CommonConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:10
 */
public class CommonConfigurable implements Configurable {
    /** 配置参数 */
    private final CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
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
        if (!dialog.getTranslateApi().equals(config.getTranslateApi())) {
            return true;
        }
        if (!dialog.getAppId().equals(config.getAppId())) {
            return true;
        }
        if (!dialog.getSecurityKey().equals(config.getSecretKey())) {
            return true;
        }
        if (!dialog.getDateClassType().equals(config.getDateClassType())) {
            return true;
        }
        if (!dialog.getApiTool().equals(config.getApiTool())) {
            return true;
        }
        if (dialog.isEnableCodeCompletion() != config.isEnableCodeCompletion()) {
            return true;
        }
        return !dialog.getCustomTemplatesPath().equals(config.getCustomTemplatesPath());
    }

    @Override
    public void apply() {
        config.setTranslateApi(dialog.getTranslateApi());
        config.setAppId(dialog.getAppId());
        config.setSecretKey(dialog.getSecurityKey());
        config.setCustomTemplatesPath(dialog.getCustomTemplatesPath());
        config.setDateClassType(dialog.getDateClassType());
        config.setApiTool(dialog.getApiTool());
        config.setEnableCodeCompletion(dialog.isEnableCodeCompletion());
        ConfigFactory.getInstance().setCommonConfig(config);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}