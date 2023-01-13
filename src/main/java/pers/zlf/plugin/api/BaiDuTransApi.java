package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REQUEST;
import pers.zlf.plugin.pojo.BaiDuTransResult;
import pers.zlf.plugin.util.HttpsUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.UrlUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        String urlStr = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=";
        // TODO 兼容低版本，没有使用URLEncoder
        urlStr = urlStr + UrlUtil.encode(query) + "&from=" + from + "&to=" + to + "&salt=" + salt + "&sign=" + sign + "&appid=" + appid;
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{HttpsUtil.X_509_TRUST_MANAGER}, null);
            URL uri = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }
            conn.setConnectTimeout(REQUEST.SOCKET_TIMEOUT);
            conn.setRequestMethod(REQUEST.GET);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return COMMON.BLANK_STRING;
            }
            BaiDuTransResult result = JsonUtil.getContentAndToObject(conn.getInputStream(), BaiDuTransResult.class);
            conn.disconnect();
            if (StringUtil.isNotEmpty(result.getError_code())) {
                throw new RuntimeException(result.getError_msg());
            }
            return result.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return COMMON.BLANK_STRING;
        }
    }
}
