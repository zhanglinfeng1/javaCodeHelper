package pers.zlf.plugin.util;

import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/18 11:44
 */
public class JsonUtil {

    public static Map<String, Object> toMap(Object obj) {
        return toMap(JSONObject.toJSONString(obj));
    }

    public static Map<String, Object> toMap(String str) {
        return JSONObject.parseObject(str, Map.class);
    }

    public static <T> T toObject(String jsonString, Class<T> cls) {
        return JSONObject.parseObject(jsonString, cls);
    }

    public static <T> T getContentAndToObject(InputStream is, Class<T> cls) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String result = br.lines().collect(Collectors.joining());
        br.close();
        is.close();
        return toObject(result,cls);
    }
}
