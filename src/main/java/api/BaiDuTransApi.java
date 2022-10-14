package api;

import constant.COMMON_CONSTANT;
import org.apache.commons.codec.digest.DigestUtils;
import pojo.TransResult;
import util.JsonUtil;
import util.StringUtil;
import util.UrlUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        String urlStr = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=";
        urlStr = urlStr + UrlUtil.encode(query) + "&from=" + from + "&to=" + to + "&salt=" + salt + "&sign=" + sign + "&appid=" + appid;
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
        URL uri = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
        }
        conn.setConnectTimeout(COMMON_CONSTANT.SOCKET_TIMEOUT);
        conn.setRequestMethod(COMMON_CONSTANT.GET);
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return COMMON_CONSTANT.BLANK_STRING;
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
        Map<String, Object> map = JsonUtil.toMap(builder.toString());
        if (StringUtil.isNotEmpty(map.get("error_code"))) {
            throw new Exception(StringUtil.toString(map.get("error_msg")));
        }
        List<TransResult> list = JsonUtil.toList(map.get("trans_result").toString(), TransResult.class);
        return list.stream().map(TransResult::getDst).collect(Collectors.joining(COMMON_CONSTANT.SEMICOLON));
    }

    private static final TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }
    };
}
