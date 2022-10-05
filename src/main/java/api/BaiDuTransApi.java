package api;

import com.alibaba.fastjson2.JSONArray;
import constant.COMMON_CONSTANT;
import org.apache.commons.codec.digest.DigestUtils;
import util.JsonUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.stream.Collectors;

public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        try {
            String urlStr = "https://api.fanyi.baidu.com/api/trans/vip/translate?appid=20221003001368411&q=";
            urlStr = urlStr + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&from=" + from + "&to=" + to + "&salt=" + salt + "&sign=" + sign;
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
            URL uri = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }
            conn.setConnectTimeout(COMMON_CONSTANT.SOCKET_TIMEOUT);
            conn.setRequestMethod(COMMON_CONSTANT.GET);
            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return COMMON_CONSTANT.BLANK_STRING;
            }
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            close(br);
            close(is);
            conn.disconnect();
            Map<String, Object> map = JsonUtil.toMap(builder.toString());
            JSONArray jsonArray = JSONArray.parseArray(map.get("trans_result").toString());
            return jsonArray.stream().map(l -> JsonUtil.toMap(l.toString()).get("dst").toString()).collect(Collectors.joining(COMMON_CONSTANT.SEMICOLON));
        } catch (Exception e) {
            e.printStackTrace();
            return COMMON_CONSTANT.BLANK_STRING;
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
