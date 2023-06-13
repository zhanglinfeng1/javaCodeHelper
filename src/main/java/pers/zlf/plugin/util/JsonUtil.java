package pers.zlf.plugin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/18 11:44
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder().create();

    public static Map<String, Object> toMap(Object obj) {
        return toMap(gson.toJson(obj));
    }

    public static Map<String, Object> toMap(String str) {
        return gson.fromJson(str, Map.class);
    }

    public static <T> T toObject(String jsonString, Class<T> cls) {
        return gson.fromJson(jsonString, cls);
    }

}
