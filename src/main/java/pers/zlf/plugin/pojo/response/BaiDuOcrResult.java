package pers.zlf.plugin.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/14 8:33
 */
public class BaiDuOcrResult extends ResponseResult {
    @SerializedName("words_result")
    private List<Words> wordsList;
    @SerializedName("words_result_num")
    private String wordsResultNum;
    @SerializedName("log_id")
    private String logId;
    @SerializedName("error_msg")
    private String errorMsg;
    @SerializedName("error_code")
    private String errorCode;

    public List<Words> getWordsList() {
        return wordsList;
    }

    public void setWordsList(List<Words> wordsList) {
        this.wordsList = wordsList;
    }

    public String getWordsResultNum() {
        return wordsResultNum;
    }

    public void setWordsResultNum(String wordsResultNum) {
        this.wordsResultNum = wordsResultNum;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public static class Words {
        private String words;

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}
