package pers.zlf.plugin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/18 11:44
 */
public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Gson FORMAT_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, Object> toMap(Object obj) {
        return toMap(GSON.toJson(obj));
    }

    public static Map<String, Object> toMap(String str) {
        return GSON.fromJson(str, Map.class);
    }

    public static <T> T toObject(String jsonString, Class<T> cls) {
        return GSON.fromJson(jsonString, cls);
    }

    public static String format(Object obj) {
        return FORMAT_GSON.toJson(obj);
    }
}
