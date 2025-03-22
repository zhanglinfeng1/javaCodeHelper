package pers.zlf.plugin.config;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.CommonConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.listener.NewCodeRemindListener;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

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
        if (dialog.getCodeRemindMinute() == 0 || dialog.getMaxCodeCompletionLength() == 0) {
            Message.notifyError(Message.CANNOT_BE_ZERO);
            return false;
        }
        if (dialog.getTranslateApi() != config.getTranslateApi()) {
            return true;
        }
        if (!StringUtil.equals(dialog.getAppId(), config.getAppId())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getSecurityKey(), config.getSecretKey())) {
            return true;
        }
        if (dialog.getApiTool() != config.getApiTool()) {
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
        if (dialog.isOpenCodeRemind() != config.isOpenCodeRemind()) {
            return true;
        }
        if (dialog.getCodeRemindMinute() != config.getCodeRemindMinute()) {
            return true;
        }
        if (dialog.getOcrApi() != config.getOcrApi()) {
            return true;
        }
        if (!StringUtil.equals(dialog.getOcrApiKey(), config.getOcrApiKey())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getOcrSecurityKey(), config.getOcrSecurityKey())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoUrl(), config.getZenTaoUrl())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoAccount(), config.getZenTaoAccount())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoPassword(), config.getZenTaoPassword())) {
            return true;
        }
        return dialog.getMaxCodeCompletionLength() != config.getMaxCodeCompletionLength();
    }

    @Override
    public void apply() {
        // 项目还没启动好
        if (!NewCodeRemindListener.isStartupCompleted()) {
            Message.notifyError(Message.PLEASE_WAIT_FOR_THE_PROJECT_TO_FINISH_LOADING);
            return;
        }
        config.setTranslateApi(dialog.getTranslateApi());
        config.setAppId(dialog.getAppId());
        config.setSecretKey(dialog.getSecurityKey());
        config.setOcrApi(dialog.getOcrApi());
        config.setOcrApiKey(dialog.getOcrApiKey());
        config.setOcrSecurityKey(dialog.getOcrSecurityKey());
        config.setApiTool(dialog.getApiTool());
        config.setEnableCodeCompletion(dialog.isEnableCodeCompletion());
        config.setOpenAngleBracket(dialog.isOpenAngleBracket());
        config.setOpenBrace(dialog.isOpenBrace());
        config.setOpenBracket(dialog.isOpenBracket());
        config.setOpenParenth(dialog.isOpenParenth());
        config.setOpenCodeRemind(dialog.isOpenCodeRemind());
        config.setCodeRemindMinute(dialog.getCodeRemindMinute());
        config.setZenTaoUrl(dialog.getZenTaoUrl());
        config.setZenTaoAccount(dialog.getZenTaoAccount());
        config.setZenTaoPassword(dialog.getZenTaoPassword());
        if (config.isOpenCodeRemind()) {
            NewCodeRemindListener.rerun(config.getCodeRemindMinute());
        } else {
            NewCodeRemindListener.shutdownNow();
        }
        ConfigFactory.getInstance().setCommonConfig(config);
    }

}