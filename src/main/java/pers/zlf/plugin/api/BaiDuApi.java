package pers.zlf.plugin.api;

import org.apache.commons.codec.digest.DigestUtils;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.response.BaiDuAiApiToken;
import pers.zlf.plugin.pojo.response.BaiDuOcrResult;
import pers.zlf.plugin.pojo.response.BaiDuTransResult;
import pers.zlf.plugin.util.FileUtil;
import pers.zlf.plugin.util.HttpUtil;
import pers.zlf.plugin.util.StringUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/05 10:34
 */
public class BaiDuApi extends BaseApi {
    private final String TRANSLATE_URL = "https://api.fanyi.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&salt=%s&sign=%s&appid=%s";
    private final String OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=";
    private final String AI_API_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token?client_id=%s&client_secret=%s&grant_type=client_credentials";

    public BaiDuApi() {
        translateApiName = "百度翻译";
    }

    @Override
    protected String requestTransApi() throws Exception {
        String appid = ConfigFactory.getInstance().getCommonConfig().getAppId();
        String securityKey = ConfigFactory.getInstance().getCommonConfig().getSecretKey();
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = DigestUtils.md5Hex(appid + text + salt + securityKey);
        String urlStr = String.format(TRANSLATE_URL, URLEncoder.encode(text, StandardCharsets.UTF_8), sourceLanguage, targetLanguage, salt, sign, appid);
        BaiDuTransResult transResult = HttpUtil.get(urlStr, BaiDuTransResult.class);
        return Optional.ofNullable(transResult.getResult()).orElseThrow(() -> new Exception(transResult.getErrorMsg()));
    }

    @Override
    protected List<String> requestOcrApi() throws Exception {
        String ocrApiKey = ConfigFactory.getInstance().getCommonConfig().getOcrApiKey();
        String ocrSecurityKey = ConfigFactory.getInstance().getCommonConfig().getOcrSecurityKey();
        if (StringUtil.isEmpty(ocrApiKey) || StringUtil.isEmpty(ocrSecurityKey)) {
            throw new Exception(Message.PLEASE_CONFIGURE_OCR_FIRST);
        }
        String param;
        if (StringUtil.isNotEmpty(filePath)) {
            String image = FileUtil.toBaiduOcrString(filePath);
            if (filePath.endsWith(FileType.PDF_FILE)) {
                if (StringUtil.isEmpty(pdfFileNum)) {
                    throw new Exception(Message.OCR_PDF_FILE_NUM_EMPTY);
                }
                param = String.join("pdf_file=%s&pdf_file_num=", image, pdfFileNum);
            } else {
                param = "image=" + image;
            }
        } else if (StringUtil.isNotEmpty(fileUrl)) {
            param = "url=" + fileUrl;
        } else {
            throw new Exception(Message.OCR_FILE_EMPTY);
        }
        BaiDuAiApiToken token = HttpUtil.post(String.format(AI_API_TOKEN_URL, ocrApiKey, ocrSecurityKey), Common.BLANK_STRING, BaiDuAiApiToken.class);
        if (StringUtil.isNotEmpty(token.getErrorDescription())) {
            throw new Exception(token.getError() + Common.COLON + token.getErrorDescription());
        }
        BaiDuOcrResult ocrResult = HttpUtil.post(String.format(OCR_URL + token.getAccessToken()), param, BaiDuOcrResult.class);
        if (StringUtil.isNotEmpty(ocrResult.getErrorCode())) {
            throw new Exception(ocrResult.getErrorCode() + Common.COLON + ocrResult.getErrorMsg());
        }
        return ocrResult.getWordsList().stream().map(BaiDuOcrResult.Words::getWords).toList();
    }

}
