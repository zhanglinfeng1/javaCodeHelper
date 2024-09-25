package pers.zlf.plugin.pojo.config;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/5 9:03
 */
public class CommonConfig {
    /** 翻译api */
    private Integer translateApi = Common.BAIDU_TRANSLATE;
    /** 翻译api appId */
    private String appId;
    /** 翻译api secretKey */
    private String secretKey;
    /** api工具 */
    private Integer apiTool = Common.SWAGGER2_API;
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

    public Integer getTranslateApi() {
        return translateApi;
    }

    public void setTranslateApi(Integer translateApi) {
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

    public Integer getApiTool() {
        return apiTool;
    }

    public void setApiTool(Integer apiTool) {
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
}