package pers.zlf.plugin.constant;

import com.intellij.psi.CommonClassNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/16 10:29
 */
public class ClassType {
    public static final String STRING = "String";
    public static final String BOOLEAN_WRAPPER = "Boolean";
    public static final String BOOLEAN = "boolean";
    public static final List<String> BASIC_TYPE_LIST = List.of("int", "short", "long", "byte", "float", "double", "boolean", "char", "Integer", "Short", "Long", "Byte", "Float",
            "Double", "Boolean", "Character", "String", "Date", "Timestamp");
    public static final Map<String, Object> DEFAULT_VALUE_MAP = new HashMap<>(16) {{
        put(CommonClassNames.JAVA_LANG_OBJECT, null);
        put(CommonClassNames.JAVA_UTIL_DATE, null);
        put(CommonClassNames.JAVA_LANG_STRING, Common.BLANK_STRING);
        put(CommonClassNames.JAVA_LANG_NUMBER, 0);
        put(CommonClassNames.JAVA_LANG_BOOLEAN, false);
        put(CommonClassNames.JAVA_LANG_BYTE, 0);
        put(CommonClassNames.JAVA_LANG_SHORT, 0);
        put(CommonClassNames.JAVA_LANG_INTEGER, 0);
        put(CommonClassNames.JAVA_LANG_LONG, 0L);
        put(CommonClassNames.JAVA_LANG_FLOAT, 0.0F);
        put(CommonClassNames.JAVA_LANG_DOUBLE, 0.0D);
        put(CommonClassNames.JAVA_LANG_CHARACTER, 0);
        put(CommonClassNames.JAVA_TIME_LOCAL_DATE, null);
        put(CommonClassNames.JAVA_TIME_LOCAL_TIME, null);
        put(CommonClassNames.JAVA_TIME_LOCAL_DATE_TIME, null);
    }};
}
