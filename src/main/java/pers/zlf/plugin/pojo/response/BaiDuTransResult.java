package pers.zlf.plugin.pojo.response;

import com.google.gson.annotations.SerializedName;
import pers.zlf.plugin.constant.Common;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 9:53
 */
public class BaiDuTransResult extends ResponseResult {
    @SerializedName("error_code")
    private String errorCode;
    @SerializedName("error_msg")
    private String errorMsg;
    @SerializedName("trans_result")
    private List<Detail> transResult;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<Detail> getTransResult() {
        return transResult;
    }

    public void setTransResult(List<Detail> transResult) {
        this.transResult = transResult;
    }

    public String getResult() {
        return Optional.ofNullable(this.transResult).map(t -> t.stream().map(Detail::getDst).collect(Collectors.joining(Common.SEMICOLON))).orElse(null);
    }

    private static class Detail {
        private String dst;

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}
