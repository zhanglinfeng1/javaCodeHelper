package pers.zlf.plugin.pojo;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/27 11:06
 */
public abstract class ResponseResult {
    /** 请求响应码*/
    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
