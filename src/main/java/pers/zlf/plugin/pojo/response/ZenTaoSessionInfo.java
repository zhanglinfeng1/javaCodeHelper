package pers.zlf.plugin.pojo.response;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/19 23:06
 */
public class ZenTaoSessionInfo extends ResponseResult {
    private String sessionID;
    private String sessionName;
    private String token;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
