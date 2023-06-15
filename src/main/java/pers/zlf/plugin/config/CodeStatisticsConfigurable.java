package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
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
        if (!CollectionUtil.equals(dialog.getFileTypeList(), commonConfig.getFileTypeList())) {
            return true;
        }
        if (!CollectionUtil.equals(dialog.getGitEmailList(), commonConfig.getGitEmailList())) {
            return true;
        }
        return dialog.isCountComment() != commonConfig.isCountComment();
    }

    @Override
    public void apply() {
        commonConfig.setFileTypeList(dialog.getFileTypeList());
        commonConfig.setGitEmailList(dialog.getGitEmailList());
        commonConfig.setCountComment(dialog.isCountComment());
        ConfigFactory.getInstance().setCommonConfig(commonConfig);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}