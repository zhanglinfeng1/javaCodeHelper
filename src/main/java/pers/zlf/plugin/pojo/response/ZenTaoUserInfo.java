package pers.zlf.plugin.pojo.response;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/19 23:06
 */
public class ZenTaoUserInfo extends ZenTaoResult {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private String id;
        private String token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
