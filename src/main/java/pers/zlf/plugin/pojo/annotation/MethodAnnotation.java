package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public class MethodAnnotation extends BaseAnnotation {

    @Override
    public String getSwaggerApi() {
        return Annotation.SWAGGER_API_OPERATION;
    }
}
