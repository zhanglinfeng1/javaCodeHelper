package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.dialog.FastJumpConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;

import javax.swing.JComponent;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/13 10:36
 */
public class FastJumpConfigurable implements Configurable {
    /** 配置参数 */
    private final CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
    /** 配置界面 */
    private final FastJumpConfigDialog dialog = new FastJumpConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return COMMON.FAST_JUMP;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getControllerFolderName().equals(commonConfig.getControllerFolderName())) {
            return true;
        }
        if (!dialog.getFeignFolderName().equals(commonConfig.getFeignFolderName())) {
            return true;
        }
        return !CollectionUtil.equals(dialog.getModuleNameList(), commonConfig.getModuleNameList());
    }

    @Override
    public void apply() {
        commonConfig.setControllerFolderName(dialog.getControllerFolderName());
        commonConfig.setFeignFolderName(dialog.getFeignFolderName());
        commonConfig.setModuleNameList(dialog.getModuleNameList());
        ConfigFactory.getInstance().setCommonConfig(commonConfig);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}