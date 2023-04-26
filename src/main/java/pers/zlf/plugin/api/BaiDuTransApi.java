package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.pojo.BaiDuTransResult;
import pers.zlf.plugin.util.HttpUtil;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.UrlUtil;

import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        String urlStr = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=";
        // TODO 兼容低版本，没有使用URLEncoder
        urlStr = urlStr + UrlUtil.encode(query) + "&from=" + from + "&to=" + to + "&salt=" + salt + "&sign=" + sign + "&appid=" + appid;
        String result = HttpUtil.get(urlStr);
        BaiDuTransResult transResult = JsonUtil.toObject(result, BaiDuTransResult.class);
        return Optional.ofNullable(transResult.getResult()).orElseThrow(() -> new Exception(transResult.getError_msg()));
    }
}
