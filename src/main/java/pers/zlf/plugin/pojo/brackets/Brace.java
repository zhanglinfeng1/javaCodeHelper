package pers.zlf.plugin.pojo.brackets;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:21
 */
public class Brace extends BaseBrackets{
    public Brace() {
        this.displayName = Common.BRACE_COLOR_DISPLAY_NAME;
        this.externalName = Common.BRACE_COLOR_KEY;
        this.tagName = Common.BRACE_COLOR_TAG_NAME;
        this.LBrackets = Common.LEFT_BRACE;
        this.RBrackets = Common.RIGHT_BRACE;
    }
}