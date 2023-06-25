package pers.zlf.plugin.constant;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 10:29
 */
public class CLASS_TYPE {
    public static final String LIST = "List";
    public static final String SET = "Set";
    public static final String ARRAYS_PATH = "java.util.Arrays";
    public static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz";
    public static final String VOID = "void";
    public static final String CLASS_FILE = ".class";
    public static final String JAVA_FILE = ".java";
    public static final String XML_FILE = ".xml";
    public static final List<String> BASIC_TYPE_LIST = List.of("int", "short", "long", "byte", "float", "double", "boolean", "char", "Integer", "Short", "Long", "Byte", "Float", "Double", "Boolean", "Character");
    public static final List<String> COMMON_TYPE_LIST = List.of("String", "Date", "Timestamp", "BigDecimal", "SimpleDateFormat", "Calendar", "LocalDate", "LocalTime", "LocalDateTime", "Logger");

}
