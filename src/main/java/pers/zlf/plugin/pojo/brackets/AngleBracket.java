package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:16
 */
public class AngleBracket extends BaseBrackets{
    public AngleBracket() {
        this.displayName = Common.ANGLE_BRACKET_COLOR_DISPLAY_NAME;
        this.externalName = Common.ANGLE_BRACKET_COLOR_KEY;
        this.tagName = Common.ANGLE_BRACKET_COLOR_TAG_NAME;
        this.LBrackets = Common.LEFT_ANGLE_BRACKET;
        this.RBrackets = Common.RIGHT_ANGLE_BRACKET;
    }
}
