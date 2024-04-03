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
    /** 自定义模板文件夹 */
    private String customTemplatesPath;
    /** 文件创建人 */
    private String author;
    /** Java日期类 */
    private Integer dateClassType = Common.DATE_CLASS_TYPE;
    /** api工具 */
    private Integer apiTool = Common.SWAGGER_API;
    /** 开启代码补全 */
    private boolean enableCodeCompletion = true;
    /** 最大代码补全展示数量 */
    private int maxCodeCompletionLength = 10;

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

    public String getCustomTemplatesPath() {
        return customTemplatesPath;
    }

    public void setCustomTemplatesPath(String customTemplatesPath) {
        this.customTemplatesPath = customTemplatesPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getDateClassType() {
        return dateClassType;
    }

    public void setDateClassType(Integer dateClassType) {
        this.dateClassType = dateClassType;
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
}