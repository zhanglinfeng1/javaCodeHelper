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
    private String feignFastJumpType;

    public CommonConfig() {
        this.feignFastJumpType = COMMON_CONSTANT.MODULAR;
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

    public String getFeignFastJumpType() {
        return feignFastJumpType;
    }

    public void setFeignFastJumpType(String feignFastJumpType) {
        this.feignFastJumpType = feignFastJumpType;
    }
}
