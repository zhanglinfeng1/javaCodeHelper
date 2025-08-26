package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/12 18:13
 */
public class ContributionDetail {
    /** git邮箱 */
    private String email;
    /** git用户名 */
    private String user;
    /** 代码行数 */
    private int codeCount;
    /** 注释行数 */
    private int commentCount;
    /** 空行 */
    private int emptyLineCount;
    /** 关键字行数 */
    private int keywordCount;

    public ContributionDetail() {
    }

    public ContributionDetail(String email, String user) {
        this.email = email;
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(int codeCount) {
        this.codeCount = codeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getEmptyLineCount() {
        return emptyLineCount;
    }

    public void setEmptyLineCount(int emptyLineCount) {
        this.emptyLineCount = emptyLineCount;
    }

    public int getKeywordCount() {
        return keywordCount;
    }

    public void setKeywordCount(int keywordCount) {
        this.keywordCount = keywordCount;
    }

    public int getTotalCount() {
        return this.codeCount + this.commentCount + this.emptyLineCount + this.keywordCount;
    }

    public String getEmailAndUser() {
        return this.email + Common.LEFT_PARENTHESES + this.user + Common.RIGHT_PARENTHESES;
    }

    public void add(ContributionDetail detail) {
        this.codeCount = this.codeCount + detail.getCodeCount();
        this.commentCount = this.commentCount + detail.getCommentCount();
        this.emptyLineCount = this.emptyLineCount + detail.getEmptyLineCount();
        this.keywordCount = this.keywordCount + detail.getKeywordCount();
    }

    public String getDadaStr() {
        String str = Common.SPACE + Common.COMMA + Common.SPACE;
        return this.codeCount + str + commentCount + str + emptyLineCount + str + keywordCount;
    }

}
