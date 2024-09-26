package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:18
 */
public class Bracket extends BaseBrackets{
    public Bracket() {
        this.displayName = Common.BRACKET_COLOR_DISPLAY_NAME;
        this.externalName = Common.BRACKET_COLOR_KEY;
        this.tagName = Common.BRACKET_COLOR_TAG_NAME;
        this.LBrackets = Common.LEFT_BRACKETS;
        this.RBrackets = Common.RIGHT_BRACKETS;
    }
}
