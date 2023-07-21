package pers.zlf.plugin.constant;

import java.util.Arrays;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/28 9:54
 */
public enum CommonEnum {
    /** 请求方式 */
    POST_REQUEST(CommonEnumType.REQUEST_TYPE, Annotation.POST_MAPPING, Request.POST),
    PUT_REQUEST(CommonEnumType.REQUEST_TYPE, Annotation.PUT_MAPPING, Request.PUT),
    GET_REQUEST(CommonEnumType.REQUEST_TYPE, Annotation.GET_MAPPING, Request.GET),
    DELETE_REQUEST(CommonEnumType.REQUEST_TYPE, Annotation.DELETE_MAPPING, Request.DELETE),
    PATCH_REQUEST(CommonEnumType.REQUEST_TYPE, Annotation.PATCH_MAPPING, Request.PATCH);

    private String type;

    private String key;

    private String value;

    CommonEnum(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public static CommonEnum select(String type, String key) {
        return Arrays.stream(CommonEnum.values()).filter(e -> e.key.equals(key) && e.type.equals(type)).findAny().orElse(null);
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
