package pers.zlf.plugin.config;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CommonConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:10
 */
public class CommonConfigurable extends BaseConfigurable<CommonConfigDialog> {
    /** 配置参数 */
    private final CommonConfig config = ConfigFactory.getInstance().getCommonConfig();

    public CommonConfigurable() {
        dialog = new CommonConfigDialog();
        displayName = Common.JAVA_CODE_HELPER;
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

}