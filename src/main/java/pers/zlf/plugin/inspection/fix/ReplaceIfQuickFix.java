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
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.pojo.SimplifyInfo;
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
public class ReplaceIfQuickFix implements LocalQuickFix {
    /** 1: 简化throw  2:简化表达式 */
    public static final int SIMPLIFY_THROW = 1;
    public static final int SIMPLIFY_EXPRESSION = 2;
    /** 替换代码后缀 */
    private final String textSuffix;
    /** 判断的对象原始名 */
    private final String variableOriginalName;
    /** 判断的对象名 */
    private String variableName;
    /** 简化类型 */
    private final int simplifyType;

    public ReplaceIfQuickFix(String variableName, String textSuffix, int simplifyType) {
        this.variableOriginalName = variableName;
        this.variableName = variableName;
        this.textSuffix = textSuffix;
        this.simplifyType = simplifyType;
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
        //替换的代码
        String text = simplifyType == SIMPLIFY_EXPRESSION ? variableOriginalName + Common.EQ_STR : Common.BLANK_STRING;
        //简化声明
        PsiReferenceExpression variableReference = (PsiReferenceExpression) MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
        SimplifyInfo simplifyInfo = MyExpressionUtil.simplifyDeclaration(variableReference);
        if (simplifyInfo.isSimplifyDeclaration()) {
            text = simplifyInfo.getDeclarationLeftText() + Common.EQ_STR;
            variableName = simplifyInfo.getDeclarationRightText();
            runnableList.add(simplifyInfo.getAssignmentElement()::delete);
        }
        //简化return
        SimplifyInfo simplifyReturnInfo = simplifyReturn(operationTokenType, nextElement);
        if (simplifyReturnInfo.isSimplifyReturn()) {
            text = Keyword.JAVA_RETURN + Common.SPACE + String.format(Common.OPTIONAL, variableName) + simplifyReturnInfo.getSimplifyReturnText() + textSuffix;
            runnableList.add(nextElement::delete);
        } else {
            text = text + String.format(Common.OPTIONAL, variableName) + textSuffix;
        }
        //替换
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

    private SimplifyInfo simplifyReturn(IElementType operationTokenType, PsiElement nextElement) {
        SimplifyInfo simplifyInfo = new SimplifyInfo();
        if (operationTokenType == JavaTokenType.EQEQ) {
            if (simplifyType == SIMPLIFY_THROW) {
                return MyExpressionUtil.simplifyReturn(nextElement, variableOriginalName);
            } else if (simplifyType == SIMPLIFY_EXPRESSION && nextElement instanceof PsiReturnStatement) {
                PsiReturnStatement returnStatement = (PsiReturnStatement) nextElement;
                String elementText = Optional.ofNullable(returnStatement.getReturnValue()).map(PsiElement::getText).orElse(Common.BLANK_STRING);
                if (variableName.equals(elementText)) {
                    simplifyInfo.setSimplifyReturn(true);
                    simplifyInfo.setSimplifyReturnText(Common.BLANK_STRING);
                }
            }
        }
        return simplifyInfo;
    }
}
