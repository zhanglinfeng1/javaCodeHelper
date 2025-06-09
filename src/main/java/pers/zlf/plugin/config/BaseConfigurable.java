package pers.zlf.plugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.dialog.BaseDialog;

import javax.swing.JComponent;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/9 12:55
 */
public abstract class BaseConfigurable<T extends BaseDialog> implements Configurable {
    /** 配置界面 */
    protected T dialog;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}
