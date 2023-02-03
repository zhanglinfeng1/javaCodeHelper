package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.COMMON;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/14 9:53
 */
public class BaiDuTransResult {
    private String error_code;
    private String error_msg;
    private List<Detail> trans_result;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public List<Detail> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<Detail> trans_result) {
        this.trans_result = trans_result;
    }

    public String getResult() {
        return Optional.ofNullable(this.trans_result).map(t -> t.stream().map(Detail::getDst).collect(Collectors.joining(COMMON.SEMICOLON))).orElse(null);
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
