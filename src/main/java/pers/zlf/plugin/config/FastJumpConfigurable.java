package pers.zlf.plugin.config;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.FastJumpConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.FastJumpConfig;
import pers.zlf.plugin.util.CollectionUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class FastJumpConfigurable extends BaseConfigurable<FastJumpConfigDialog> {
    /** 配置参数 */
    private final FastJumpConfig config = ConfigFactory.getInstance().getFastJumpConfig();

    public FastJumpConfigurable() {
        dialog = new FastJumpConfigDialog();
        displayName = Common.FAST_JUMP;
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

}