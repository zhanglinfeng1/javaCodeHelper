package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 16:22
 */
public class ControllerAnnotation extends BaseAnnotation {

    @Override
    protected String getSwagger2Api() {
        return Annotation.SWAGGER2_API;
    }

    @Override
    protected String getSwagger3Api() {
        return Annotation.SWAGGER3_TAG;
    }

    @Override
    protected String getSwagger2Text() {
        return super.getSwagger2Text().replaceFirst(Annotation.VALUE, Annotation.TAGS);
    }

}
