package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.BaiDuTransResult;
import pers.zlf.plugin.util.HttpUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/05 10:34
 */
public class BaiDuTransApi {

    public String trans(String appid, String securityKey, String query, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + query + salt + securityKey);
        String urlStr = String.format(Common.BAIDU_TRANSLATE_URL, URLEncoder.encode(query, StandardCharsets.UTF_8), from, to, salt, sign, appid);
        BaiDuTransResult transResult = HttpUtil.get(urlStr, BaiDuTransResult.class);
        return Optional.ofNullable(transResult.getResult()).orElseThrow(() -> new Exception(transResult.getError_msg()));
    }
}
