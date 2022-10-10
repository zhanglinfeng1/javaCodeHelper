package util;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/18 11:44
 */
public class JsonUtil {

    public static Map<String, Object> toMap(Object obj) {
        return toMap(new Gson().toJson(obj));
    }

    public static Map<String, Object> toMap(String str) {
        return new Gson().fromJson(str, Map.class);
    }

}
