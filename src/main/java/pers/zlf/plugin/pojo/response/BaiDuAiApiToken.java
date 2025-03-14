package pers.zlf.plugin.pojo.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/13 23:10
 */
public class BaiDuAiApiToken extends ResponseResult {
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("session_key")
    private String sessionKey;
    @SerializedName("access_token")
    private String accessToken;
    private String scope;
    @SerializedName("session_secret")
    private String sessionSecret;
    private String error;
    @SerializedName("error_description")
    private String errorDescription;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSessionSecret() {
        return sessionSecret;
    }

    public void setSessionSecret(String sessionSecret) {
        this.sessionSecret = sessionSecret;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}

