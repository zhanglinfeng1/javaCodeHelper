package config;

import com.intellij.openapi.options.Configurable;
import constant.COMMON;
import dialog.CommonConfigDialog;
import factory.ConfigFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pojo.CommonConfig;

import javax.swing.JComponent;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/5 9:10
 */
public class CommonConfigurable implements Configurable {
    /** 配置参数 */
    private final CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
    /** 配置界面 */
    private final CommonConfigDialog dialog = new CommonConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return COMMON.JAVA_CODE_HELP;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getApi().equals(commonConfig.getApi())) {
            return true;
        }
        if (!dialog.getAppId().equals(commonConfig.getAppId())) {
            return true;
        }
        if (!dialog.getSecurityKey().equals(commonConfig.getSecretKey())) {
            return true;
        }
        if (!dialog.getFastJumpType().equals(commonConfig.getFastJumpType())) {
            return true;
        }
        if (!dialog.getControllerFolderName().equals(commonConfig.getControllerFolderName())) {
            return true;
        }
        if (!dialog.getFeignFolderName().equals(commonConfig.getFeignFolderName())) {
            return true;
        }
        return !dialog.getCustomTemplatesPath().equals(commonConfig.getCustomTemplatesPath());
    }

    @Override
    public void apply() {
        commonConfig.setApi(dialog.getApi());
        commonConfig.setAppId(dialog.getAppId());
        commonConfig.setSecretKey(dialog.getSecurityKey());
        commonConfig.setFastJumpType(dialog.getFastJumpType());
        commonConfig.setControllerFolderName(dialog.getControllerFolderName());
        commonConfig.setFeignFolderName(dialog.getFeignFolderName());
        commonConfig.setCustomTemplatesPath(dialog.getCustomTemplatesPath());
        ConfigFactory.getInstance().updateCommonConfig(commonConfig);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}