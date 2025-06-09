package pers.zlf.plugin.config;

import pers.zlf.plugin.dialog.FastJumpConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.FastJumpConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class FastJumpConfigurable extends BaseConfigurable<FastJumpConfigDialog> {
    /** 配置参数 */
    private final FastJumpConfig CONFIG = ConfigFactory.getInstance().getFastJumpConfig();

    public FastJumpConfigurable() {
        dialog = new FastJumpConfigDialog();
    }

    @Override
    public boolean isModified() {
        if (!StringUtil.equals(dialog.getControllerFolderName(), CONFIG.getControllerFolderName())) {
            return true;
        }
        if (!StringUtil.equals(dialog.getFeignFolderName(), CONFIG.getFeignFolderName())) {
            return true;
        }
        return !CollectionUtil.equals(dialog.getModuleNameList(), CONFIG.getModuleNameList());
    }

    @Override
    public void apply() {
        CONFIG.setControllerFolderName(dialog.getControllerFolderName());
        CONFIG.setFeignFolderName(dialog.getFeignFolderName());
        CONFIG.setModuleNameList(dialog.getModuleNameList());
        ConfigFactory.getInstance().setFastJumpConfig(CONFIG);
    }

}