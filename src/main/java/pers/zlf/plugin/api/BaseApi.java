package pers.zlf.plugin.api;

import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/4 11:43
 */
public abstract class BaseApi {
    protected String translateApiName;
    protected String translateUrl;
    /** 原文 */
    protected String text;
    /** 原文语言 */
    protected String sourceLanguage;
    /** 译文语言 */
    protected String targetLanguage;
    /** 英文 */
    private final String EN = "en";
    /** 英文 */
    private final String ZH = "zh";

    public String trans(String text) throws Exception {
        this.text = text;
        this.sourceLanguage = ZH;
        this.targetLanguage = EN;
        if (StringUtil.isEnglish(this.text)) {
            this.sourceLanguage = EN;
            this.targetLanguage = ZH;
        }
        if (checkTrans()) {
            return requestTransApi();
        }
        throw new Exception(Message.PLEASE_CONFIGURE_TRANSLATE_FIRST);
    }

    protected abstract boolean checkTrans();

    protected abstract String requestTransApi() throws Exception;

    public String getTranslateApiName() {
        return translateApiName;
    }
}
