package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.FastJumpConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.FastJumpConfig;
import pers.zlf.plugin.util.CollectionUtil;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class FastJumpConfigurable implements Configurable {
    /** 配置参数 */
    private final FastJumpConfig config = ConfigFactory.getInstance().getFastJumpConfig();
    /** 配置界面 */
    private final FastJumpConfigDialog dialog = new FastJumpConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Common.FAST_JUMP;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getControllerFolderName().equals(config.getControllerFolderName())) {
            return true;
        }
        if (!dialog.getFeignFolderName().equals(config.getFeignFolderName())) {
            return true;
        }
        return !CollectionUtil.equals(dialog.getModuleNameList(), config.getModuleNameList());
    }

    @Override
    public void apply() {
        config.setControllerFolderName(dialog.getControllerFolderName());
        config.setFeignFolderName(dialog.getFeignFolderName());
        config.setModuleNameList(dialog.getModuleNameList());
        ConfigFactory.getInstance().setFastJumpConfig(config);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}