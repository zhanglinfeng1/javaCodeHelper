package constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 10:29
 */
public class TYPE_CONSTANT {
    public static final String LIST = "List";
    public static final List<String> LIST_TYPE_LIST = Arrays.asList("ArrayList", "LinkedList");
    public static final String MAP = "Map";
    public static final List<String> MAP_TYPE_LIST = Arrays.asList("HashMap", "LinkedHashMap");
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int", "short", "long", "byte", "float", "double", "boolean", "char", "Integer", "Short", "Long", "Byte", "Float", "Double", "Boolean", "Character");
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String", "Date", "Timestamp", "BigDecimal");
    public static final Map<String, String> TYPE_MAP = new HashMap<>() {{
        put("LinkedList", "java.util.LinkedList");
        put("ArrayList", "java.util.ArrayList");
        put("HashMap", "java.util.HashMap");
        put("LinkedHashMap", "java.util.LinkedHashMap");
    }};

    /** 泛型 */
    public static final List<String> GENERIC_PARADIGM_LIST = Arrays.asList("T", "E", "?");

}
