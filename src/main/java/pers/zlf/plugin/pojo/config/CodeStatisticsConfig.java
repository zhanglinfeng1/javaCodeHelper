package pers.zlf.plugin.pojo.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
public class CodeStatisticsConfig {
    /** 统计空白行 */
    private boolean countEmptyLine = false;
    /** 统计注释 */
    private boolean countComment = true;
    /** 统计关键字 */
    private boolean countKeyword = true;
    /** 参与代码行数统计的文件类型 */
    private List<String> fileTypeList = new ArrayList<>();
    /** 参与统计的git邮箱 */
    private List<String> gitEmailList = new ArrayList<>();

    public boolean isCountEmptyLine() {
        return countEmptyLine;
    }

    public void setCountEmptyLine(boolean countEmptyLine) {
        this.countEmptyLine = countEmptyLine;
    }

    public boolean isCountComment() {
        return countComment;
    }

    public void setCountComment(boolean countComment) {
        this.countComment = countComment;
    }

    public boolean isCountKeyword() {
        return countKeyword;
    }

    public void setCountKeyword(boolean countKeyword) {
        this.countKeyword = countKeyword;
    }

    public List<String> getFileTypeList() {
        return fileTypeList;
    }

    public void setFileTypeList(List<String> fileTypeList) {
        this.fileTypeList = fileTypeList;
    }

    public List<String> getGitEmailList() {
        return gitEmailList;
    }

    public void setGitEmailList(List<String> gitEmailList) {
        this.gitEmailList = gitEmailList;
    }
}
