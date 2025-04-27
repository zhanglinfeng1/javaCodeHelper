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

    public final Icon BRIGHT_ICON;

    public final Icon DARKI_CON;

    IconEnum(Icon brightIcon, Icon darkIcon) {
        this.BRIGHT_ICON = brightIcon;
        this.DARKI_CON = darkIcon;
    }

}
