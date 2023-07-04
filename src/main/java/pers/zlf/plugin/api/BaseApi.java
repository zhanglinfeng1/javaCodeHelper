package pers.zlf.plugin.api;

import com.intellij.openapi.ui.Messages;
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
    protected String sourceLanguage = Common.ZH;
    /** 译文语言 */
    protected String targetLanguage = Common.EN;

    public String trans(String text) throws Exception {
        this.text = text;
        if (StringUtil.isEnglish(this.text)) {
            sourceLanguage = Common.EN;
            targetLanguage = Common.ZH;
        }
        if (checkTrans()) {
            return requestTransApi();
        }
        Messages.showMessageDialog(Message.TRANSLATION_CONFIGURATION, Common.BLANK_STRING, Messages.getInformationIcon());
        return Common.BLANK_STRING;
    }

    protected abstract boolean checkTrans();

    protected abstract String requestTransApi() throws Exception;

}
