package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.action.CodeLineCountAction;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.dialog.CodeStatisticsDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;

import javax.swing.JComponent;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/13 10:36
 */
public class CodeStatisticsConfigurable implements Configurable {
    /** 配置参数 */
    private final CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
    /** 配置界面 */
    private final CodeStatisticsDialog dialog = new CodeStatisticsDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return COMMON.CODE_STATISTICS;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        boolean isModified = false;
        boolean reCount = false;
        if (!CollectionUtil.equals(dialog.getFileTypeList(), commonConfig.getFileTypeList())) {
            isModified = true;
            reCount = true;
        } else if (!CollectionUtil.equals(dialog.getGitEmailList(), commonConfig.getGitEmailList())) {
            isModified = true;
        } else if (dialog.isRealTimeStatistics() != commonConfig.isRealTimeStatistics()) {
            isModified = true;
            reCount = true;
        } else if (dialog.isCountComment() != commonConfig.isCountComment()) {
            isModified = true;
            reCount = true;
        }
        //重新统计
        if (reCount) {
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                CodeLineCountAction.countCodeLines(project);
            }
        }
        return isModified;
    }

    @Override
    public void apply() {
        commonConfig.setFileTypeList(dialog.getFileTypeList());
        commonConfig.setGitEmailList(dialog.getGitEmailList());
        commonConfig.setCountComment(dialog.isCountComment());
        commonConfig.setRealTimeStatistics(dialog.isRealTimeStatistics());
        ConfigFactory.getInstance().setCommonConfig(commonConfig);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}