package pers.zlf.plugin.config;

import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.CommonConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.listener.ScheduledTasksListener;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:10
 */
public class CommonConfigurable extends BaseConfigurable<CommonConfigDialog> {
    /** 配置参数 */
    private final CommonConfig CONFIG = ConfigFactory.getInstance().getCommonConfig();

    public CommonConfigurable() {
        dialog = new CommonConfigDialog();
    }

    @Override
    public boolean isModified() {
        if (dialog.getCodeRemindMinute() == 0 || dialog.getMaxCodeCompletionLength() == 0) {
            Message.notifyError(Message.CANNOT_BE_ZERO);
            return false;
        }
        if (dialog.getTranslateApi() != CONFIG.getTranslateApi()) {
            return true;
        }
        if (!StringUtil.equals(dialog.getAppId(), CONFIG.getAppId())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getSecurityKey(), CONFIG.getSecretKey())) {
            return true;
        }
        if (dialog.getApiTool() != CONFIG.getApiTool()) {
            return true;
        }
        if (dialog.isEnableCodeCompletion() != CONFIG.isEnableCodeCompletion()) {
            return true;
        }
        if (dialog.isOpenAngleBracket() != CONFIG.isOpenAngleBracket()) {
            return true;
        }
        if (dialog.isOpenBrace() != CONFIG.isOpenBrace()) {
            return true;
        }
        if (dialog.isOpenBracket() != CONFIG.isOpenBracket()) {
            return true;
        }
        if (dialog.isOpenParenth() != CONFIG.isOpenParenth()) {
            return true;
        }
        if (dialog.isOpenCodeRemind() != CONFIG.isOpenCodeRemind()) {
            return true;
        }
        if (dialog.getCodeRemindMinute() != CONFIG.getCodeRemindMinute()) {
            return true;
        }
        if (dialog.isOpenZenTaoRemind() != CONFIG.isOpenZenTaoRemind()) {
            return true;
        }
        if (dialog.getZenTaoRemindMinute() != CONFIG.getZenTaoRemindMinute()) {
            return true;
        }
        if (dialog.getOcrApi() != CONFIG.getOcrApi()) {
            return true;
        }
        if (!StringUtil.equals(dialog.getOcrApiKey(), CONFIG.getOcrApiKey())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getOcrSecurityKey(), CONFIG.getOcrSecurityKey())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoUrl(), CONFIG.getZenTaoUrl())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoAccount(), CONFIG.getZenTaoAccount())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getZenTaoPassword(), CONFIG.getZenTaoPassword())) {
            return true;
        }
        return dialog.getMaxCodeCompletionLength() != CONFIG.getMaxCodeCompletionLength();
    }

    @Override
    public void apply() {
        // 项目还没启动好
        if (!ScheduledTasksListener.isStartupCompleted()) {
            Message.notifyError(Message.PLEASE_WAIT_FOR_THE_PROJECT_TO_FINISH_LOADING);
            return;
        }
        CONFIG.setTranslateApi(dialog.getTranslateApi());
        CONFIG.setAppId(dialog.getAppId());
        CONFIG.setSecretKey(dialog.getSecurityKey());
        CONFIG.setOcrApi(dialog.getOcrApi());
        CONFIG.setOcrApiKey(dialog.getOcrApiKey());
        CONFIG.setOcrSecurityKey(dialog.getOcrSecurityKey());
        CONFIG.setApiTool(dialog.getApiTool());
        CONFIG.setEnableCodeCompletion(dialog.isEnableCodeCompletion());
        CONFIG.setOpenAngleBracket(dialog.isOpenAngleBracket());
        CONFIG.setOpenBrace(dialog.isOpenBrace());
        CONFIG.setOpenBracket(dialog.isOpenBracket());
        CONFIG.setOpenParenth(dialog.isOpenParenth());
        CONFIG.setOpenCodeRemind(dialog.isOpenCodeRemind());
        CONFIG.setCodeRemindMinute(dialog.getCodeRemindMinute());
        CONFIG.setZenTaoUrl(dialog.getZenTaoUrl());
        CONFIG.setZenTaoAccount(dialog.getZenTaoAccount());
        CONFIG.setZenTaoPassword(dialog.getZenTaoPassword());
        if (CONFIG.isOpenCodeRemind()) {
            ScheduledTasksListener.rerunNewCodeRemind(CONFIG.getCodeRemindMinute());
        } else {
            ScheduledTasksListener.shutdownNowNewCodeRemind();
        }
        if (CONFIG.isOpenZenTaoRemind()) {
            ScheduledTasksListener.rerunZenTaoRemind(CONFIG.getZenTaoRemindMinute());
        } else {
            ScheduledTasksListener.shutdownZenTaoRemind();
        }
        ConfigFactory.getInstance().setCommonConfig(CONFIG);
    }

}