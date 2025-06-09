package pers.zlf.plugin.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
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
    private final CodeStatisticsConfig CONFIG = ConfigFactory.getInstance().getCodeStatisticsConfig();

    public CodeStatisticsConfigurable() {
        dialog = new CodeStatisticsDialog();
    }

    @Override
    public boolean isModified() {
        if (!CollectionUtil.equals(dialog.getFileTypeList(), CONFIG.getFileTypeList())) {
            return true;
        }
        if (!CollectionUtil.equals(dialog.getGitEmailList(), CONFIG.getGitEmailList())) {
            return true;
        }
        if (dialog.isCountEmptyLine() != CONFIG.isCountEmptyLine()) {
            return true;
        }
        if (dialog.isCountKeyword() != CONFIG.isCountKeyword()) {
            return true;
        }
        if (!StringUtil.equals(dialog.getCountDate(), CONFIG.getCountDate())) {
            return true;
        }
        return dialog.isCountComment() != CONFIG.isCountComment();
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
        CONFIG.setFileTypeList(dialog.getFileTypeList());
        CONFIG.setGitEmailList(dialog.getGitEmailList());
        CONFIG.setCountComment(dialog.isCountComment());
        CONFIG.setCountEmptyLine(dialog.isCountEmptyLine());
        CONFIG.setCountKeyword(dialog.isCountKeyword());
        CONFIG.setCountDate(dialog.getCountDate());
        ConfigFactory.getInstance().setCodeStatisticsConfig(CONFIG);
        // 重新统计
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            CodeCountUtil.countCodeLines(project);
        }
    }

}