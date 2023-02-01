package pers.zlf.plugin.constant;

import java.util.Arrays;

public enum MESSAGE_ENUM {
    /** 代码检查 */
    CODE_INSPECTION_DATE(MESSAGE_ENUM_TYPE.CODE_INSPECTION, "java.util.Date", "(JavaCodeHelp) Replace Date with LocalDateTime"),
    CODE_INSPECTION_SIMPLE_DATE_FORMAT(MESSAGE_ENUM_TYPE.CODE_INSPECTION, "java.text.SimpleDateFormat", "(JavaCodeHelp) Replace SimpleDateFormat with DateTimeFormatter");

    private String type;

    private String key;

    private String value;

    MESSAGE_ENUM(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public static MESSAGE_ENUM select(String type, String key) {
        return Arrays.stream(MESSAGE_ENUM.values()).filter(e -> e.key.equals(key) && e.type.equals(type)).findAny().orElse(null);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
