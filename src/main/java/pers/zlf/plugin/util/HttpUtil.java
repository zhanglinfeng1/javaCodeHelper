package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Request;
import pers.zlf.plugin.pojo.response.ResponseResult;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/12/22 18:14
 */
public class HttpUtil {
    private static final String TLS = "TLS";

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

    public static <T extends ResponseResult> T get(String urlStr, Class<T> cls) throws Exception {
        HttpURLConnection conn = getConnection(urlStr);
        conn.setRequestMethod(Request.GET);
        return getResult(conn, cls);
    }

    public static <T extends ResponseResult> T post(String urlStr, String output, Class<T> cls) throws Exception {
        HttpURLConnection conn = getConnection(urlStr);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod(Request.POST);
        if (StringUtil.isNotEmpty(output)) {
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(output.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }
        return getResult(conn, cls);
    }

    private static HttpURLConnection getConnection(String urlStr) throws Exception {
        //TODO  JDK21  URI.toURL
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            SSLContext sslcontext = SSLContext.getInstance(TLS);
            sslcontext.init(null, new TrustManager[]{HttpUtil.X_509_TRUST_MANAGER}, null);
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
        }
        conn.setConnectTimeout(Request.SOCKET_TIMEOUT);
        return conn;
    }

    private static <T extends ResponseResult> T getResult(HttpURLConnection conn, Class<T> cls) throws Exception {
        String result = Common.BLANK_STRING;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = br.lines().collect(Collectors.joining());
            br.close();
        }
        conn.disconnect();
        T t = JsonUtil.toObject(result, cls);
        t.setResponseCode(conn.getResponseCode());
        return t;
    }
}
