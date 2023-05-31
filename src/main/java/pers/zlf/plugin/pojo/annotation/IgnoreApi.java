package pers.zlf.plugin.pojo.annotation;

import pers.zlf.plugin.constant.ANNOTATION;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 18:49
 */
public class IgnoreApi extends BasicApi {

    @Override
    public String getSwaggerApi() {
        return ANNOTATION.SPRINGFOX_API_IGNORE;
    }

    public String toString() {
        return name;
    }
}
