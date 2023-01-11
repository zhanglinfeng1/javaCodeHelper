package pers.zlf.plugin.util;

import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

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
        return JSONObject.parseObject(jsonString,cls);
    }
}
