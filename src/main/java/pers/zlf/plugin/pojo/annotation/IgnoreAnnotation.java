package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 18:49
 */
public class IgnoreAnnotation extends BaseAnnotation {

    @Override
    protected String getSwagger2Api() {
        return Annotation.SPRINGFOX_API_IGNORE;
    }

    @Override
    protected String getSwagger3Api() {
        return Annotation.SWAGGER3_PARAMETER;
    }

    @Override
    protected String getSwagger2Text() {
        return name;
    }

    @Override
    protected String getSwagger3Text() {
        return this.name + String.format(Common.FILLING_STR3, Annotation.HIDDEN, true);
    }
}
