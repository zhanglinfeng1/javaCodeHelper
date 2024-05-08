package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.factory.ConfigFactory;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public abstract class BaseAnnotation {
    /** 注解名称 */
    protected final String name;
    /** 注解路径 */
    private String qualifiedName;
    /** 注释 */
    private String value;
    /** 是否必须 */
    private boolean required;

    public BaseAnnotation() {
        Integer apiTool = ConfigFactory.getInstance().getCommonConfig().getApiTool();
        if (Common.SWAGGER2_API.equals(apiTool)) {
            qualifiedName = getSwagger2Api();
        }else if (Common.SWAGGER3_API.equals(apiTool)){
            qualifiedName = getSwagger3Api();
        }
        String[] annotationPathArr = this.qualifiedName.split(Regex.DOT);
        this.name = annotationPathArr[annotationPathArr.length - 1];
    }

    /**
     * 获取Swagger2注解路径
     *
     * @return String
     */
    protected abstract String getSwagger2Api();

    /**
     * 获取Swagger3注解路径
     *
     * @return String
     */
    protected abstract String getSwagger3Api();

    public void setValue(String value) {
        this.value = value;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * 获取注解文本
     *
     * @return String
     */
    public String getText() {
        Integer apiTool = ConfigFactory.getInstance().getCommonConfig().getApiTool();
        if (Common.SWAGGER2_API.equals(apiTool)) {
            return getSwagger2Text();
        } else if (Common.SWAGGER3_API.equals(apiTool)) {
            return getSwagger3Text();
        }
        return Common.BLANK_STRING;
    }

    protected String getSwagger2Text() {
        return getSwaggerText(Annotation.VALUE);
    }

    protected String getSwagger3Text() {
        return getSwaggerText(Annotation.NAME);
    }

    private String getSwaggerText(String attributeName) {
        if (required) {
            return this.name + String.format(Common.FILLING_STR2, attributeName, value, Annotation.REQUIRED, true);
        } else {
            return this.name + String.format(Common.FILLING_STR, attributeName, value);
        }
    }
}
