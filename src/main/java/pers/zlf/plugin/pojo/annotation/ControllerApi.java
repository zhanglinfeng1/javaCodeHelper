package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.ANNOTATION;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 16:22
 */
public class ControllerApi extends BasicApi {

    @Override
    public String getSwaggerApi() {
        return ANNOTATION.SWAGGER_API;
    }

    public String toString() {
        return super.toString().replaceFirst(ANNOTATION.VALUE, ANNOTATION.TAGS);
    }

}
