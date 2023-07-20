package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.action.CodeLineCountAction;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.CodeStatisticsDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CollectionUtil;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class CodeStatisticsConfigurable implements Configurable {
    /** 配置参数 */
    private final CodeStatisticsConfig config = ConfigFactory.getInstance().getCodeStatisticsConfig();
    /** 配置界面 */
    private final CodeStatisticsDialog dialog = new CodeStatisticsDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Common.CODE_STATISTICS;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!CollectionUtil.equals(dialog.getFileTypeList(), config.getFileTypeList())) {
            return true;
        }
        if (!CollectionUtil.equals(dialog.getGitEmailList(), config.getGitEmailList())) {
            return true;
        }
        if (dialog.isRealTimeStatistics() != config.isRealTimeStatistics()) {
            return true;
        }
        if (dialog.isCountEmptyLine() != config.isCountEmptyLine()) {
            return true;
        }
        if (dialog.isCountKeyword() != config.isCountKeyword()) {
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
        config.setRealTimeStatistics(dialog.isRealTimeStatistics());
        ConfigFactory.getInstance().setCodeStatisticsConfig(config);
        // 重新统计
        if (config.isRealTimeStatistics()){
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                CodeLineCountAction.countCodeLines(project);
            }
        }
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}