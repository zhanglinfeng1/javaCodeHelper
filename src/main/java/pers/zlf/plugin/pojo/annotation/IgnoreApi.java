package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.Annotation;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 18:49
 */
public class IgnoreApi extends BaseApi {

    @Override
    public String getSwaggerApi() {
        return Annotation.SPRINGFOX_API_IGNORE;
    }

    @Override
    public String toString() {
        return name;
    }
}
