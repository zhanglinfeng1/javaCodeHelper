package pers.zlf.plugin.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.dialog.CodeStatisticsDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CodeCountUtil;
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
        if (!StringUtil.equals(dialog.getCountDate(), config.getCountDate())) {
            return true;
        }
        return dialog.isCountComment() != config.isCountComment();
    }

    @Override
    public void apply() {
        String countDate = dialog.getCountDate();
        if (StringUtil.isNotEmpty(countDate)) {
            if (null == DateUtil.parse(countDate, DateUtil.YYYY_MM_DD)) {
                dialog.clearCountDate();
                Message.notifyError(Message.DATE_FORMAT_ERROR);
                return;
            }
        }
        config.setFileTypeList(dialog.getFileTypeList());
        config.setGitEmailList(dialog.getGitEmailList());
        config.setCountComment(dialog.isCountComment());
        config.setCountEmptyLine(dialog.isCountEmptyLine());
        config.setCountKeyword(dialog.isCountKeyword());
        config.setCountDate(dialog.getCountDate());
        ConfigFactory.getInstance().setCodeStatisticsConfig(config);
        // 重新统计
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            CodeCountUtil.countCodeLines(project);
        }
    }

}