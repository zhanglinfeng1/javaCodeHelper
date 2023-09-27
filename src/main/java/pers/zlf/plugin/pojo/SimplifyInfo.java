package pers.zlf.plugin.pojo;

import com.intellij.psi.PsiElement;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/9/15 14:51
 */
public class SimplifyInfo {
    /** 可以简化声明 */
    private boolean simplifyDeclaration;
    /** 声明左边文本 */
    private String declarationLeftText;
    /** 声明右边文本 */
    private String declarationRightText;
    /** 赋值语句 */
    private PsiElement assignmentElement;
    /** 可以简化return */
    private boolean simplifyReturn;
    /** return简化文本 */
    private String simplifyReturnText;

    public boolean isSimplifyDeclaration() {
        return simplifyDeclaration;
    }

    public void setSimplifyDeclaration(boolean simplifyDeclaration) {
        this.simplifyDeclaration = simplifyDeclaration;
    }

    public String getDeclarationLeftText() {
        return declarationLeftText;
    }

    public void setDeclarationLeftText(String declarationLeftText) {
        this.declarationLeftText = declarationLeftText;
    }

    public String getDeclarationRightText() {
        return declarationRightText;
    }

    public void setDeclarationRightText(String declarationRightText) {
        this.declarationRightText = declarationRightText;
    }

    public boolean isSimplifyReturn() {
        return simplifyReturn;
    }

    public void setSimplifyReturn(boolean simplifyReturn) {
        this.simplifyReturn = simplifyReturn;
    }

    public String getSimplifyReturnText() {
        return simplifyReturnText;
    }

    public void setSimplifyReturnText(String simplifyReturnText) {
        this.simplifyReturnText = simplifyReturnText;
    }

    public PsiElement getAssignmentElement() {
        return assignmentElement;
    }

    public void setAssignmentElement(PsiElement assignmentElement) {
        this.assignmentElement = assignmentElement;
    }

    public void dealDeclarationInfo(String declarationLeftText, String declarationRightText, PsiElement assignmentElement) {
        if (StringUtil.isNotEmpty(declarationLeftText) && StringUtil.isNotEmpty(declarationRightText)) {
            this.simplifyDeclaration = true;
            this.declarationLeftText = declarationLeftText;
            this.declarationRightText = declarationRightText;
            this.assignmentElement = assignmentElement;
        }
    }
}
