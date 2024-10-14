package pers.zlf.plugin.constant;

import javax.swing.Icon;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/28 9:54
 */
public enum IconEnum {
    //新增按钮图标
    ADD(MyIcon.ADD, MyIcon.ADD_DARK),
    //删除按钮图标
    REMOVE(MyIcon.REMOVE, MyIcon.REMOVE_DARK),
    //编辑按钮图标
    EDIT(MyIcon.EDIT, MyIcon.EDIT_DARK),
    //重置按钮图标
    RESET(MyIcon.RESET, MyIcon.RESET_DARK);

    private final Icon brightIcon;

    private final Icon darkIcon;

    IconEnum(Icon brightIcon, Icon darkIcon) {
        this.brightIcon = brightIcon;
        this.darkIcon = darkIcon;
    }

    public Icon getBrightIcon() {
        return brightIcon;
    }

    public Icon getDarkIcon() {
        return darkIcon;
    }

}
