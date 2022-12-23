package api;

import com.alibaba.fastjson2.JSONObject;
import constant.COMMON;
import constant.REQUEST;
import org.apache.commons.codec.digest.DigestUtils;
import pojo.BaiDuTransResult;
import util.HttpsUtil;
import util.StringUtil;
import util.UrlUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) throws RuntimeException {
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
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            is.close();
            conn.disconnect();
            BaiDuTransResult result = JSONObject.parseObject(builder.toString(), BaiDuTransResult.class);
            if (StringUtil.isNotEmpty(result.getError_code())) {
                throw new RuntimeException(result.getError_msg());
            }
            return result.getResult();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
