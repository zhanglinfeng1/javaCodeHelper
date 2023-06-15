package pers.zlf.plugin.pojo;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/15 14:23
 */
public class CommentCheckResult {
    /** 段落注释 */
    private boolean paragraphComment;

    public CommentCheckResult() {
        paragraphComment = false;
    }

    public boolean isParagraphComment() {
        return paragraphComment;
    }

    public void setParagraphComment(boolean paragraphComment) {
        this.paragraphComment = paragraphComment;
    }

}
