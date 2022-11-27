package constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 10:29
 */
public class TYPE_CONSTANT {
    public static final String LIST_PATH = "java.util.List";
    public static final String ARRAYS_PATH = "java.util.Arrays";
    public static final List<String> BASIC_TYPE_LIST = Arrays.asList("int", "short", "long", "byte", "float", "double", "boolean", "char", "Integer", "Short", "Long", "Byte", "Float", "Double", "Boolean", "Character");
    public static final List<String> COMMON_TYPE_LIST = Arrays.asList("String", "Date", "Timestamp", "BigDecimal");

    /** 泛型 */
    public static final List<String> GENERIC_PARADIGM_LIST = Arrays.asList("T", "E", "?", "<");

}
