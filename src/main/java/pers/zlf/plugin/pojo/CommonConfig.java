package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.COMMON;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/5 9:03
 */
public class CommonConfig {
    /** 翻译api */
    private Integer apiType = COMMON.BAIDU_TRANSLATE;
    /** 翻译api appId */
    private String appId;
    /** 翻译api secretKey */
    private String secretKey;
    /** 自定义模板文件夹 */
    private String customTemplatesPath;
    /** Java日期类 */
    private Integer dateClassType = COMMON.DATE_CLASS_TYPE;
    /** controller所在文件夹名 */
    private String controllerFolderName;
    /** feign所在文件夹名 */
    private String feignFolderName;
    /** 网关模块名 */
    private List<String> moduleNameList = new ArrayList<>();

    public Integer getApiType() {
        return apiType;
    }

    public void setApiType(Integer apiType) {
        this.apiType = apiType;
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

    public Integer getDateClassType() {
        return dateClassType;
    }

    public void setDateClassType(Integer dateClassType) {
        this.dateClassType = dateClassType;
    }

    public String getControllerFolderName() {
        return controllerFolderName;
    }

    public void setControllerFolderName(String controllerFolderName) {
        this.controllerFolderName = controllerFolderName;
    }

    public String getFeignFolderName() {
        return feignFolderName;
    }

    public void setFeignFolderName(String feignFolderName) {
        this.feignFolderName = feignFolderName;
    }

    public List<String> getModuleNameList() {
        return moduleNameList;
    }

    public void setModuleNameList(List<String> moduleNameList) {
        this.moduleNameList = moduleNameList;
    }
}