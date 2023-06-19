package pers.zlf.plugin.pojo;

import com.intellij.ide.projectView.PresentationData;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/19 15:41
 */
public class CodeStatisticsInfo {
    /** 模块名 */
    private String moduleName;
    /** 备注对象 */
    private PresentationData data;
    /** 旧备注 */
    private String oldLocationString;
    /** 代码行数 */
    private int lineCount;
    /** 根据git记录统计的总行数 */
    private int totalGitLineCount;
    /** 我提交git的行数 */
    private int myGitLineCount;

    public CodeStatisticsInfo() {
    }

    public CodeStatisticsInfo(String moduleName, String oldLocationString) {
        this.moduleName = moduleName;
        this.oldLocationString = oldLocationString;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public PresentationData getData() {
        return data;
    }

    public void setData(PresentationData data) {
        this.data = data;
    }

    public String getOldLocationString() {
        return oldLocationString;
    }

    public void setOldLocationString(String oldLocationString) {
        this.oldLocationString = oldLocationString;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getTotalGitLineCount() {
        return totalGitLineCount;
    }

    public void setTotalGitLineCount(int totalGitLineCount) {
        this.totalGitLineCount = totalGitLineCount;
    }

    public int getMyGitLineCount() {
        return myGitLineCount;
    }

    public void setMyGitLineCount(int myGitLineCount) {
        this.myGitLineCount = myGitLineCount;
    }

}
