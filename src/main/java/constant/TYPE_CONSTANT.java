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
    public static final List<String> MAP_TYPE_LIST = Arrays.asList("HashMap", "LinkedMap");
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int", "short", "long", "byte", "float", "double", "boolean", "char", "Integer", "Short", "Long", "Byte", "Float", "Double", "Boolean", "Character");
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String", "Date", "Timestamp", "BigDecimal");
    public static final Map<String, String> BASIC_TYPE_MAP = new HashMap<>() {{
        put("int", "Integer");
        put("short", "Short");
        put("long", "Long");
        put("byte", "Byte");
        put("float", "Float");
        put("double", "Double");
        put("boolean", "Boolean");
        put("char", "Character");
    }};

}
