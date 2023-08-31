package pers.zlf.plugin.pojo.psi;

import com.intellij.psi.PsiDeclarationStatement;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/30 17:59
 */
public class PsiDeclarationStatementModel {
    /** 声明语句的左边代码 */
    private String leftText;
    /** 声明语句的右边代码 */
    private String rightText;

    public PsiDeclarationStatementModel(PsiDeclarationStatement declarationStatement) {
        String declarationText = declarationStatement.getText();
        int index = declarationText.indexOf(Common.EQ_STR.trim());
        if (index == -1 || index == declarationText.length()) {
            return;
        }
        this.leftText = declarationText.substring(0, index);
        String rightText = declarationText.substring(index + 1).trim();
        if (!rightText.endsWith(Common.SEMICOLON)) {
            return;
        }
        this.rightText = rightText.substring(0, rightText.length() - 1);
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public boolean exist() {
        return StringUtil.isNotEmpty(leftText) && StringUtil.isNotEmpty(rightText);
    }
}
