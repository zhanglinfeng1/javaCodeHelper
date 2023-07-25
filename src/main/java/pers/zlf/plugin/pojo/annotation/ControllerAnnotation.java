package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public class ControllerAnnotation extends BaseAnnotation {

    @Override
    public String getSwaggerApi() {
        return Annotation.SWAGGER_API;
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst(Annotation.VALUE, Annotation.TAGS);
    }

}
