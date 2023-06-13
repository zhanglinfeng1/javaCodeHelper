package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REQUEST;
import pers.zlf.plugin.util.lambda.Equals;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/12/22 18:14
 */
public class HttpUtil {
    private static final TrustManager X_509_TRUST_MANAGER = new X509TrustManager() {

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

    public static String get(String urlStr) throws Exception {
        String result = COMMON.BLANK_STRING;
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{HttpUtil.X_509_TRUST_MANAGER}, null);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        Equals.of(conn instanceof HttpsURLConnection).ifTrue(() -> ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory()));
        conn.setConnectTimeout(REQUEST.SOCKET_TIMEOUT);
        conn.setRequestMethod(REQUEST.GET);
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = br.lines().collect(Collectors.joining());
            br.close();
        }
        conn.disconnect();
        return result;
    }
}
