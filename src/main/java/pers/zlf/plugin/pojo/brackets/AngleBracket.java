package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:16
 */
public class AngleBracket extends BaseBrackets{
    public AngleBracket() {
        this.displayName = "尖括号//颜色";
        this.externalName = Common.ANGLE_BRACKET_COLOR_KEY;
        this.tagName = "JKH";
        this.LBrackets = Common.LEFT_ANGLE_BRACKET;
        this.RBrackets = Common.RIGHT_ANGLE_BRACKET;
    }
}
