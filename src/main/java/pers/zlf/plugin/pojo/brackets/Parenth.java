package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:20
 */
public class Parenth extends BaseBrackets{
    public Parenth() {
        this.displayName = Common.PARENTH_COLOR_DISPLAY_NAME;
        this.externalName = Common.PARENTH_COLOR_KEY;
        this.tagName = Common.PARENTH_COLOR_TAG_NAME;
        this.LBrackets = Common.LEFT_PARENTHESES;
        this.RBrackets = Common.RIGHT_PARENTHESES;
    }
}
