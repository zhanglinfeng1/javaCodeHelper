package pers.zlf.plugin.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/18 17:49
 */
public class ReplaceQuickFix implements LocalQuickFix {
    /** 提示信息 */
    private final String name;
    /** 替换代码前缀 */
    private String textPrefix;
    /** 替换代码后缀 */
    private String textSuffix;
    /** 删除声明语句 */
    private boolean deleteDeclaration;
    /** 需要简化return语句 */
    private boolean needSimplifyReturn;
    /** 判断的对象名 */
    private String variableName;

    public ReplaceQuickFix(String name, String variableName) {
        this.textPrefix = Common.BLANK_STRING;
        this.name = name;
        this.variableName = variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setTextPrefix(String textPrefix) {
        this.textPrefix = textPrefix;
    }

    public void setTextSuffix(String textSuffix) {
        this.textSuffix = textSuffix;
    }

    public void setDeleteDeclaration(boolean deleteDeclaration) {
        this.deleteDeclaration = deleteDeclaration;
    }

    public void setNeedSimplifyReturn(boolean needSimplifyReturn) {
        this.needSimplifyReturn = needSimplifyReturn;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiIfStatement ifStatement = (PsiIfStatement) descriptor.getPsiElement().getParent();
        PsiElement nextElement = MyPsiUtil.getTheNextNonBlankElement(ifStatement);
        //添加元素
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) ifStatement.getCondition();
        assert binaryExpression != null;
        IElementType operationTokenType = binaryExpression.getOperationTokenType();
        addPsiElement(operationTokenType, ifStatement, nextElement);
        //导入java.util.Optional
        PsiFile psiFile = ifStatement.getContainingFile();
        MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL);
        //简化return
        String simplifyReturnText = null;
        if (needSimplifyReturn && operationTokenType == JavaTokenType.EQEQ) {
            simplifyReturnText = MyExpressionUtil.simplifyReturn(nextElement, variableName);
            //删除return语句
            if (simplifyReturnText != null) {
                this.textPrefix = Keyword.JAVA_RETURN + Common.SPACE;
                nextElement.delete();
            }
        }
        //删除声明语句
        if (deleteDeclaration) {
            PsiReferenceExpression variableReference = (PsiReferenceExpression) MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
            assert variableReference != null;
            PsiLocalVariable variable = (PsiLocalVariable) variableReference.resolve();
            assert variable != null;
            variable.getParent().delete();
        }
        //替换
        String text = String.format(Common.OPTIONAL, variableName) + Optional.ofNullable(simplifyReturnText).orElse(Common.BLANK_STRING);
        text = this.textPrefix + text + this.textSuffix;
        PsiElement newElement = new CommentTracker().replaceAndRestoreComments(ifStatement, text);
        CodeStyleManager.getInstance(project).reformat(newElement);
    }

    private void addPsiElement(IElementType operationTokenType, PsiIfStatement ifStatement, PsiElement nextElement) {
        PsiElement parentElement = ifStatement.getParent();
        if (operationTokenType == JavaTokenType.NE && ifStatement.getElseBranch() != null) {
            PsiStatement thenStatement = ifStatement.getThenBranch();
            if (thenStatement instanceof PsiBlockStatement) {
                PsiBlockStatement thenCodeBlock = (PsiBlockStatement) thenStatement;
                for (PsiElement element : thenCodeBlock.getCodeBlock().getChildren()) {
                    String text = element.getText();
                    if (StringUtil.isEmpty(text) || Common.LEFT_BRACE.equals(text) || Common.RIGHT_BRACE.equals(text)) {
                        continue;
                    }
                    parentElement.addBefore(element, nextElement);
                }
            }
        }
    }

}
