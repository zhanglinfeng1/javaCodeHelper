package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 16:22
 */
public class ControllerApi extends BasicApi {

    @Override
    public void selectApi() {
        if (COMMON.SWAGGER_API.equals(apiType)) {
            qualifiedName = ANNOTATION.SWAGGER_API;
        }
    }

    public String toString() {
        return name + COMMON.LEFT_PARENTHESES + ANNOTATION.TAGS + COMMON.EQ_STR + COMMON.DOUBLE_QUOTATION_MARK + getValue() + COMMON.DOUBLE_QUOTATION_MARK + COMMON.RIGHT_PARENTHESES;
    }

}
