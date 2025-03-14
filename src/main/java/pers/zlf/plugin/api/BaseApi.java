package pers.zlf.plugin.api;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/4 11:43
 */
public abstract class BaseApi {
    protected String translateApiName;
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
    /** 本地文件路径 */
    protected String filePath;
    /** 网络文件路径 */
    protected String fileUrl;
    /** 需要识别的PDF文件页码 */
    protected String pdfFileNum;

    /**
     * 翻译
     *
     * @param text 待翻译文本
     * @return 翻译结果
     * @throws Exception 异常
     */
    public String trans(String text) throws Exception {
        this.text = text;
        this.sourceLanguage = ZH;
        this.targetLanguage = EN;
        if (StringUtil.isEnglish(this.text)) {
            this.sourceLanguage = EN;
            this.targetLanguage = ZH;
        }
        return requestTransApi();
    }

    /**
     * 文字识别
     *
     * @param filePath   本地文件路径
     * @param fileUrl    网络文件路径
     * @param pdfFileNum PDF文件页码
     * @return 识别结果
     * @throws Exception 异常
     */
    public List<String> ocr(String filePath, String fileUrl, String pdfFileNum) throws Exception {
        this.filePath = Empty.of(filePath).orElse(Common.BLANK_STRING);
        this.fileUrl = Empty.of(fileUrl).orElse(Common.BLANK_STRING);
        this.pdfFileNum = Empty.of(pdfFileNum).orElse(Common.BLANK_STRING);
        return requestOcrApi();
    }

    /**
     * 请求翻译api
     *
     * @return 翻译结果
     * @throws Exception 异常
     */
    protected abstract String requestTransApi() throws Exception;

    /**
     * 请求文字识别api
     *
     * @return 识别结果
     * @throws Exception 异常
     */
    protected abstract List<String> requestOcrApi() throws Exception;

    public String getTranslateApiName() {
        return translateApiName;
    }
}
