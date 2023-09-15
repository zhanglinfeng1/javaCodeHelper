package pers.zlf.plugin.pojo;

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
}
