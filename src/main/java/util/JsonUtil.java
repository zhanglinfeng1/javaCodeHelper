package util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;
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

    public static <T> List<T> toList(String jsonString, Class<T> cls) {
        return JSONArray.parseArray(jsonString).toList(cls);
    }
}
