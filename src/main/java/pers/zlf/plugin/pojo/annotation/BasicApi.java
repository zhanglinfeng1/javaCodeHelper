package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.factory.ConfigFactory;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 16:22
 */
public abstract class BasicApi {
    protected final String name;
    private String qualifiedName;
    private String value;
    private boolean required;

    public BasicApi() {
        if (COMMON.SWAGGER_API.equals(ConfigFactory.getInstance().getCommonConfig().getApiTool())) {
            qualifiedName = getSwaggerApi();
        }
        String[] annotationPathArr = this.qualifiedName.split(REGEX.DOT);
        this.name = annotationPathArr[annotationPathArr.length - 1];
    }

    public abstract String getSwaggerApi();

    public void setValue(String value) {
        this.value = value;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String toString() {
        String result = this.name + COMMON.LEFT_PARENTHESES + ANNOTATION.VALUE + COMMON.EQ_STR + COMMON.DOUBLE_QUOTATION + value + COMMON.DOUBLE_QUOTATION;
        if (required) {
            result = result + COMMON.COMMA + ANNOTATION.REQUIRED + COMMON.EQ_STR + true;
        }
        return result + COMMON.RIGHT_PARENTHESES;
    }

}
