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
        return Common.JAVA_CODE_HELPER;
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
        if (!dialog.getApiTool().equals(config.getApiTool())) {
            return true;
        }
        if (dialog.isEnableCodeCompletion() != config.isEnableCodeCompletion()) {
            return true;
        }
        if (dialog.isOpenAngleBracket() != config.isOpenAngleBracket()) {
            return true;
        }
        if (dialog.isOpenBrace() != config.isOpenBrace()) {
            return true;
        }
        if (dialog.isOpenBracket() != config.isOpenBracket()) {
            return true;
        }
        if (dialog.isOpenParenth() != config.isOpenParenth()) {
            return true;
        }
        return !dialog.getMaxCodeCompletionLength().equals(config.getMaxCodeCompletionLength());
    }

    @Override
    public void apply() {
        config.setTranslateApi(dialog.getTranslateApi());
        config.setAppId(dialog.getAppId());
        config.setSecretKey(dialog.getSecurityKey());
        config.setApiTool(dialog.getApiTool());
        config.setEnableCodeCompletion(dialog.isEnableCodeCompletion());
        config.setOpenAngleBracket(dialog.isOpenAngleBracket());
        config.setOpenBrace(dialog.isOpenBrace());
        config.setOpenBracket(dialog.isOpenBracket());
        config.setOpenParenth(dialog.isOpenParenth());
        ConfigFactory.getInstance().setCommonConfig(config);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}