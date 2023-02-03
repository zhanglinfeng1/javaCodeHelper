package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REQUEST;
import pers.zlf.plugin.pojo.BaiDuTransResult;
import pers.zlf.plugin.util.HttpsUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.UrlUtil;
import pers.zlf.plugin.util.lambda.Equals;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        String urlStr = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=";
        // TODO 兼容低版本，没有使用URLEncoder
        urlStr = urlStr + UrlUtil.encode(query) + "&from=" + from + "&to=" + to + "&salt=" + salt + "&sign=" + sign + "&appid=" + appid;
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{HttpsUtil.X_509_TRUST_MANAGER}, null);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        Equals.of(conn instanceof HttpsURLConnection).ifTrue(() -> ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory()));
        conn.setConnectTimeout(REQUEST.SOCKET_TIMEOUT);
        conn.setRequestMethod(REQUEST.GET);
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return COMMON.BLANK_STRING;
        }
        BaiDuTransResult result = JsonUtil.getContentAndToObject(conn.getInputStream(), BaiDuTransResult.class);
        conn.disconnect();
        Optional.ofNullable(result.getResult()).orElseThrow(() -> new Exception(result.getError_msg()));
        return result.getResult();
    }
}
