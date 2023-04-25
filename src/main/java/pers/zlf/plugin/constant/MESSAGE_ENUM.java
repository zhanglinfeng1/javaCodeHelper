package pers.zlf.plugin.constant;

import java.util.Arrays;

public enum MESSAGE_ENUM {
    /** 请求方式 */
    POST_REQUEST(MESSAGE_ENUM_TYPE.REQUEST_TYPE, ANNOTATION.POST_MAPPING, REQUEST.POST),
    PUT_REQUEST(MESSAGE_ENUM_TYPE.REQUEST_TYPE, ANNOTATION.PUT_MAPPING, REQUEST.PUT),
    GET_REQUEST(MESSAGE_ENUM_TYPE.REQUEST_TYPE, ANNOTATION.GET_MAPPING, REQUEST.GET),
    DELETE_REQUEST(MESSAGE_ENUM_TYPE.REQUEST_TYPE, ANNOTATION.DELETE_MAPPING, REQUEST.DELETE);

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
