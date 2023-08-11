package pers.zlf.plugin.pojo;

import com.intellij.ide.projectView.PresentationData;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/19 15:41
 */
public class CodeStatisticsInfo {
    /** 项目名 */
    private String projectName;
    /** 备注对象 */
    private PresentationData data;
    /** 旧备注 */
    private String oldLocationString;
    /** 代码行数 */
    private int lineCount;
    /** 总git代码行数 */
    private int totalGitLineCount;
    /** 我的git代码行数 */
    private int myGitLineCount;
    /** 贡献率 */
    private String contributionRate;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public String getContributionRate() {
        return contributionRate;
    }

    public void setContributionRate(String contributionRate) {
        this.contributionRate = contributionRate;
    }

    public void dealPresentationData(PresentationData data) {
        this.data = data;
        this.oldLocationString = StringUtil.toString(data.getLocationString());
    }
}
