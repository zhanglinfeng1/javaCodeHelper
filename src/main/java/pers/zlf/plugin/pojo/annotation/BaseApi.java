package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.factory.ConfigFactory;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public abstract class BaseApi {
    protected final String name;
    private String qualifiedName;
    private String value;
    private boolean required;

    public BaseApi() {
        if (Common.SWAGGER_API.equals(ConfigFactory.getInstance().getCommonConfig().getApiTool())) {
            qualifiedName = getSwaggerApi();
        }
        String[] annotationPathArr = this.qualifiedName.split(Regex.DOT);
        this.name = annotationPathArr[annotationPathArr.length - 1];
    }

    /**
     * 获取注解路径
     *
     * @return String
     */
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
    
    @Override
    public String toString() {
        String result = this.name + Common.LEFT_PARENTHESES + Annotation.VALUE + Common.EQ_STR + Common.DOUBLE_QUOTATION + value + Common.DOUBLE_QUOTATION;
        if (required) {
            result = result + Common.COMMA + Annotation.REQUIRED + Common.EQ_STR + true;
        }
        return result + Common.RIGHT_PARENTHESES;
    }

}
