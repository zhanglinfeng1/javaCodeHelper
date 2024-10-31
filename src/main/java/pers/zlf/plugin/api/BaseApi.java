package pers.zlf.plugin.api;

import com.intellij.openapi.application.ApplicationManager;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/4 11:43
 */
public abstract class BaseApi {
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
        ApplicationManager.getApplication().invokeLater(() -> Message.showMessage(Message.TRANSLATION_CONFIGURATION));
        return Common.BLANK_STRING;
    }

    protected abstract boolean checkTrans();

    protected abstract String requestTransApi() throws Exception;

    public abstract String getTranslateApiName();
}
