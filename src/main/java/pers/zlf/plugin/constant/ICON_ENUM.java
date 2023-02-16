package pers.zlf.plugin.constant;

import javax.swing.Icon;

public enum ICON_ENUM {
    ADD(ICON.ADD, ICON.ADD_DARK),
    REMOVE(ICON.REMOVE, ICON.REMOVE_DARK),
    EDIT(ICON.EDIT, ICON.EDIT_DARK);

    private Icon brightIcon;

    private Icon darkIcon;

    ICON_ENUM(Icon brightIcon, Icon darkIcon) {
        this.brightIcon = brightIcon;
        this.darkIcon = darkIcon;
    }

    public Icon getBrightIcon() {
        return brightIcon;
    }

    public void setBrightIcon(Icon brightIcon) {
        this.brightIcon = brightIcon;
    }

    public Icon getDarkIcon() {
        return darkIcon;
    }

    public void setDarkIcon(Icon darkIcon) {
        this.darkIcon = darkIcon;
    }
}
