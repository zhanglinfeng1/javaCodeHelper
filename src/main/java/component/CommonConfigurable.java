package component;

import com.intellij.openapi.options.Configurable;
import dialog.CommonConfigDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import pojo.Config;

import javax.swing.JComponent;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/5 9:10
 */
public class CommonConfigurable implements Configurable {

    private final Config config = ConfigComponent.getInstance().getState();
    private final CommonConfigDialog dialog = new CommonConfigDialog();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "CommonConfig";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        dialog.reset();
        return dialog.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!dialog.getApi().equals(config.getApi())) {
            return true;
        }
        if (!dialog.getAppId().equals(config.getAppId())) {
            return true;
        }
        if (!dialog.getSecurityKey().equals(config.getSecretKey())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() {
        config.setApi(dialog.getApi());
        config.setAppId(dialog.getAppId());
        config.setSecretKey(dialog.getSecurityKey());
    }

    @Override
    public void reset() {
        dialog.reset();
    }
}