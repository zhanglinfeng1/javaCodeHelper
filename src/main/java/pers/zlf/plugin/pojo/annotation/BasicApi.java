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
    public final int apiType = ConfigFactory.getInstance().getCommonConfig().getApiTool();
    public final String name;
    public String qualifiedName;
    private String value;
    public boolean required;

    public BasicApi() {
        selectApi();
        String[] annotationPathArr = this.qualifiedName.split(REGEX.DOT);
        this.name = annotationPathArr[annotationPathArr.length - 1];
    }

    public abstract void selectApi();

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String toString() {
        String result = this.name + COMMON.LEFT_PARENTHESES + ANNOTATION.VALUE + COMMON.EQ_STR + COMMON.DOUBLE_QUOTATION_MARK + value + COMMON.DOUBLE_QUOTATION_MARK;
        if (required) {
            result = result + COMMON.COMMA + ANNOTATION.REQUIRED + COMMON.EQ_STR + true;
        }
        return result + COMMON.RIGHT_PARENTHESES;
    }

}
