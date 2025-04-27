package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:18
 */
public class Bracket extends BaseBrackets{
    public Bracket() {
        this.displayName = "中括号//颜色";
        this.externalName = Common.BRACKET_COLOR_KEY;
        this.tagName = "ZKH";
        this.LBrackets = Common.LEFT_BRACKETS;
        this.RBrackets = Common.RIGHT_BRACKETS;
    }
}
