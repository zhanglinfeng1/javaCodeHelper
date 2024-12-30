package pers.zlf.plugin.pojo.config;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:03
 */
public class CommonConfig {
    /** 翻译api */
    private int translateApi = Common.BAIDU_TRANSLATE;
    /** 翻译api appId */
    private String appId;
    /** 翻译api secretKey */
    private String secretKey;
    /** api工具 */
    private int apiTool = Common.SWAGGER2_API;
    /** 开启代码补全 */
    private boolean enableCodeCompletion = true;
    /** 最大代码补全展示数量 */
    private int maxCodeCompletionLength = 10;
    /** <> */
    private boolean openAngleBracket = true;
    /** () */
    private boolean openParenth = true;
    /** [] */
    private boolean openBracket = true;
    /** {} */
    private boolean openBrace = true;
    /** 开启git代码拉取提醒 */
    private boolean openCodeRemind = true;
    /** 轮询时间（分钟） */
    private int codeRemindMinute = 10;

    public int getTranslateApi() {
        return translateApi;
    }

    public void setTranslateApi(int translateApi) {
        this.translateApi = translateApi;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getApiTool() {
        return apiTool;
    }

    public void setApiTool(int apiTool) {
        this.apiTool = apiTool;
    }

    public boolean isEnableCodeCompletion() {
        return enableCodeCompletion;
    }

    public void setEnableCodeCompletion(boolean enableCodeCompletion) {
        this.enableCodeCompletion = enableCodeCompletion;
    }

    public int getMaxCodeCompletionLength() {
        return maxCodeCompletionLength;
    }

    public void setMaxCodeCompletionLength(int maxCodeCompletionLength) {
        this.maxCodeCompletionLength = maxCodeCompletionLength;
    }

    public boolean isOpenAngleBracket() {
        return openAngleBracket;
    }

    public void setOpenAngleBracket(boolean openAngleBracket) {
        this.openAngleBracket = openAngleBracket;
    }

    public boolean isOpenParenth() {
        return openParenth;
    }

    public void setOpenParenth(boolean openParenth) {
        this.openParenth = openParenth;
    }

    public boolean isOpenBracket() {
        return openBracket;
    }

    public void setOpenBracket(boolean openBracket) {
        this.openBracket = openBracket;
    }

    public boolean isOpenBrace() {
        return openBrace;
    }

    public void setOpenBrace(boolean openBrace) {
        this.openBrace = openBrace;
    }

    public boolean isOpenCodeRemind() {
        return openCodeRemind;
    }

    public void setOpenCodeRemind(boolean openCodeRemind) {
        this.openCodeRemind = openCodeRemind;
    }

    public int getCodeRemindMinute() {
        return codeRemindMinute;
    }

    public void setCodeRemindMinute(int codeRemindMinute) {
        this.codeRemindMinute = codeRemindMinute;
    }
}