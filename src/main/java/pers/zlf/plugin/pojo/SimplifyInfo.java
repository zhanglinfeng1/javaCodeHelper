package pers.zlf.plugin.pojo;

import com.intellij.psi.PsiReturnStatement;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/31 15:32
 */
public class SimplifyInfo {
    /** return语句 */
    private PsiReturnStatement returnStatement;
    /** 可以简化 */
    private boolean simplify;
    /** 简化后的代码 */
    private String simplifyText;

    public PsiReturnStatement getReturnStatement() {
        return returnStatement;
    }

    public void setReturnStatement(PsiReturnStatement returnStatement) {
        this.returnStatement = returnStatement;
    }

    public boolean isSimplify() {
        return simplify;
    }

    public void setSimplify(boolean simplify) {
        this.simplify = simplify;
    }

    public String getSimplifyText() {
        return simplifyText;
    }

    public void setSimplifyText(String simplifyText) {
        this.simplifyText = simplifyText;
    }

}
