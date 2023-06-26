package pers.zlf.plugin.pojo;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/15 13:49
 */
public class CommentFormat {
    /** 段落注释 */
    private boolean paragraphComment;
    /** 注释前缀 */
    private List<String> commentList;
    /** 注释段落前缀 */
    private List<String> commentPrefixList;
    /** 注释段落后缀 */
    private List<String> commentSuffixList;

    public CommentFormat() {
        this.paragraphComment = false;
    }

    public CommentFormat(List<String> commentList, List<String> commentPrefixList, List<String> commentSuffixList) {
        this.paragraphComment = false;
        this.commentList = commentList;
        this.commentPrefixList = commentPrefixList;
        this.commentSuffixList = commentSuffixList;
    }

    public boolean isParagraphComment() {
        return paragraphComment;
    }

    public void setParagraphComment(boolean paragraphComment) {
        this.paragraphComment = paragraphComment;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<String> commentList) {
        this.commentList = commentList;
    }

    public List<String> getCommentPrefixList() {
        return commentPrefixList;
    }

    public void setCommentPrefixList(List<String> commentPrefixList) {
        this.commentPrefixList = commentPrefixList;
    }

    public List<String> getCommentSuffixList() {
        return commentSuffixList;
    }

    public void setCommentSuffixList(List<String> commentSuffixList) {
        this.commentSuffixList = commentSuffixList;
    }
}
