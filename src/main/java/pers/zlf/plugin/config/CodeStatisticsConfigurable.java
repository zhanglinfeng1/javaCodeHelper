package pers.zlf.plugin.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import pers.zlf.plugin.action.CodeLineCountAction;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.CodeStatisticsDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class CodeStatisticsConfigurable extends BaseConfigurable<CodeStatisticsDialog> {
    /** 配置参数 */
    private final CodeStatisticsConfig config = ConfigFactory.getInstance().getCodeStatisticsConfig();

    public CodeStatisticsConfigurable() {
        dialog = new CodeStatisticsDialog();
        displayName = Common.CODE_STATISTICS;
    }

    @Override
    public boolean isModified() {
        if (!CollectionUtil.equals(dialog.getFileTypeList(), config.getFileTypeList())) {
            return true;
        }
        if (!CollectionUtil.equals(dialog.getGitEmailList(), config.getGitEmailList())) {
            return true;
        }
        if (dialog.isCountEmptyLine() != config.isCountEmptyLine()) {
            return true;
        }
        if (dialog.isCountKeyword() != config.isCountKeyword()) {
            return true;
        }
        if (!dialog.getCountDate().equals(config.getCountDate())) {
            return true;
        }
        return dialog.isCountComment() != config.isCountComment();
    }

    @Override
    public void apply() {
        config.setFileTypeList(dialog.getFileTypeList());
        config.setGitEmailList(dialog.getGitEmailList());
        config.setCountComment(dialog.isCountComment());
        config.setCountEmptyLine(dialog.isCountEmptyLine());
        config.setCountKeyword(dialog.isCountKeyword());
        String countDate = dialog.getCountDate();
        if (StringUtil.isNotEmpty(countDate)) {
            if (null == DateUtil.parse(countDate, DateUtil.YYYY_MM_DD)) {
                Message.showMessage(Message.DATE_FORMAT_ERROR);
                return;
            }
        }
        config.setCountDate(dialog.getCountDate());
        ConfigFactory.getInstance().setCodeStatisticsConfig(config);
        // 重新统计
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            CodeLineCountAction.countCodeLines(project);
        }
    }

}