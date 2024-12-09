package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.response.BaiDuTransResult;
import pers.zlf.plugin.util.HttpUtil;
import pers.zlf.plugin.util.StringUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/05 10:34
 */
public class BaiDuApi extends BaseApi {
    private String appid;
    private String securityKey;

    public BaiDuApi() {
        translateApiName = "百度翻译";
        translateUrl = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&salt=%s&sign=%s&appid=%s";
    }

    @Override
    protected boolean checkTrans() {
        this.appid = ConfigFactory.getInstance().getCommonConfig().getAppId();
        this.securityKey = ConfigFactory.getInstance().getCommonConfig().getSecretKey();
        return StringUtil.isNotEmpty(this.appid) && StringUtil.isNotEmpty(this.securityKey);
    }

    @Override
    protected String requestTransApi() throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + text + salt + securityKey);
        String urlStr = String.format(translateUrl, URLEncoder.encode(text, StandardCharsets.UTF_8), sourceLanguage, targetLanguage, salt, sign, appid);
        BaiDuTransResult transResult = HttpUtil.get(urlStr, BaiDuTransResult.class);
        return Optional.ofNullable(transResult.getResult()).orElseThrow(() -> new Exception(transResult.getErrorMsg()));
    }

}
