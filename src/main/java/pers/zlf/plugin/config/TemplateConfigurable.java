package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.TemplateConfigDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.TemplateConfig;
import pers.zlf.plugin.util.MapUtil;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2024/4/8 11:42
 */
public class TemplateConfigurable implements Configurable {
    /** 配置参数 */
    private final TemplateConfig config = ConfigFactory.getInstance().getTemplateConfig();
    /** 配置界面 */
    private final TemplateConfigDialog dialog = new TemplateConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Common.TEMPLATE_CONFIG;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getAuthor().equals(config.getAuthor())) {
            return true;
        }
        return !MapUtil.equals(dialog.getTemplateMap(), config.getTemplateMap());
    }

    @Override
    public void apply() {
        config.setAuthor(dialog.getAuthor());
        config.setTemplateMap(dialog.getTemplateMap());
        ConfigFactory.getInstance().setTemplateConfig(config);
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}