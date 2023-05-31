package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.ANNOTATION;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 16:22
 */
public class ModelApi extends BasicApi {

    @Override
    public String getSwaggerApi() {
        return ANNOTATION.SWAGGER_API_MODEL;
    }

}
