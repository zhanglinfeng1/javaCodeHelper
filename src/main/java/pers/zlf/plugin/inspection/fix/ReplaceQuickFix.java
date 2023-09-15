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
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/18 17:49
 */
public class ReplaceQuickFix implements LocalQuickFix {
    /** 替换代码前缀 */
    private String textPrefix = Common.BLANK_STRING;
    /** 替换代码后缀 */
    private String textSuffix;
    /** 判断的对象原始名 */
    private final String variableOriginalName;
    /** 判断的对象名 */
    private String variableName;
    /** 删除声明语句 */
    private boolean deleteDeclaration;
    /** 需要简化return语句 */
    private boolean needSimplifyReturn;

    public ReplaceQuickFix(String variableName) {
        this.variableOriginalName = variableName;
        this.variableName = variableName;
    }

    public void setTextPrefix(String textPrefix) {
        this.textPrefix = textPrefix;
    }

    public void setTextSuffix(String textSuffix) {
        this.textSuffix = textSuffix;
    }

    public void dealDeclarationInfo(String declarationLeftText, String declarationRightText) {
        this.deleteDeclaration = true;
        this.textPrefix = declarationLeftText + Common.EQ_STR;
        this.variableName = declarationRightText;
    }

    public void setNeedSimplifyReturn(boolean needSimplifyReturn) {
        this.needSimplifyReturn = needSimplifyReturn;
    }

    public boolean isDeleteDeclaration() {
        return deleteDeclaration;
    }

    @NotNull
    @Override
    public String getName() {
        return Message.OPTIONAL_FIX_NAME;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiIfStatement ifStatement = (PsiIfStatement) descriptor.getPsiElement().getParent();
        PsiFile psiFile = ifStatement.getContainingFile();
        PsiElement nextElement = MyPsiUtil.getTheNextNonBlankElement(ifStatement);
        //添加元素
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) ifStatement.getCondition();
        IElementType operationTokenType = binaryExpression.getOperationTokenType();
        addPsiElement(operationTokenType, ifStatement, nextElement);
        //执行方法
        List<Runnable> runnableList = new ArrayList<>();
        //简化return
        String simplifyReturnText = null;
        if (needSimplifyReturn && operationTokenType == JavaTokenType.EQEQ) {
            simplifyReturnText = MyExpressionUtil.simplifyReturn(nextElement, variableOriginalName);
        }
        //删除声明语句
        if (deleteDeclaration) {
            PsiReferenceExpression variableReference = (PsiReferenceExpression) MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
            PsiLocalVariable variable = (PsiLocalVariable) variableReference.resolve();
            runnableList.add(variable.getParent()::delete);
        }
        //删除return语句
        if (simplifyReturnText != null) {
            textPrefix = Keyword.JAVA_RETURN + Common.SPACE;
            runnableList.add(nextElement::delete);
        }
        //替换
        String text = String.format(Common.OPTIONAL, variableName) + Optional.ofNullable(simplifyReturnText).orElse(Common.BLANK_STRING);
        text = textPrefix + text + textSuffix;
        PsiElement newElement = new CommentTracker().replaceAndRestoreComments(ifStatement, text);
        CodeStyleManager.getInstance(project).reformat(newElement);
        //导入java.util.Optional
        MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL);
        runnableList.forEach(Runnable::run);
    }

    private void addPsiElement(IElementType operationTokenType, PsiIfStatement ifStatement, PsiElement nextElement) {
        PsiElement parentElement = ifStatement.getParent();
        if (operationTokenType != JavaTokenType.NE || ifStatement.getElseBranch() == null) {
            return;
        }
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
