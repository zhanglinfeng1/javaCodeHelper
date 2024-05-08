package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public class ParameterAnnotation extends BaseAnnotation {

    @Override
    protected String getSwagger2Api() {
        return Annotation.SWAGGER2_API_PARAM;
    }

    @Override
    protected String getSwagger3Api() {
        return Annotation.SWAGGER3_PARAMETER;
    }

}
