package pers.zlf.plugin.pojo;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/27 11:06
 */
public abstract class BaseResponseResult {
    /** 请求响应码*/
    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
