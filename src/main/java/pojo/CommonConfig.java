package pojo;

import constant.COMMON_CONSTANT;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/5 9:03
 */
public class CommonConfig {
    private String api;
    private String appId;
    private String secretKey;
    private String fastJumpType;
    private String controllerFolderName;
    private String feignFolderName;


    public CommonConfig() {
        this.fastJumpType = COMMON_CONSTANT.MODULAR;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
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

    public String getFastJumpType() {
        return fastJumpType;
    }

    public void setFastJumpType(String fastJumpType) {
        this.fastJumpType = fastJumpType;
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

}
