package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.function.Predicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 10:18
 */
public class OptionalInspection extends BaseInspection {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement statement) {
                PsiExpression condition = statement.getCondition();
                //二元表达式，单个if，无else分支
                boolean simpleIfStatement = condition instanceof PsiBinaryExpression && statement.getParent() instanceof PsiCodeBlock && statement.getElseBranch() == null;
                if (!simpleIfStatement) {
                    return;
                }
                //获取变量名
                String variableName = getVariableName((PsiBinaryExpression) condition);
                if (StringUtil.isEmpty(variableName)) {
                    return;
                }
                //获取抛出的异常
                String throwText = getExceptionText(statement.getThenBranch());
                if (StringUtil.isEmpty(throwText)) {
                    return;
                }
                //快速解决方案
                String mapText = Common.BLANK_STRING;
                ReplaceQuickFix quickFix = getQuickFix(statement);
                PsiElement nextElement = getNextElement(statement);
                //简化return
                if (nextElement instanceof PsiReturnStatement) {
                    mapText = simplifyReturn((PsiReturnStatement) nextElement, variableName, quickFix);
                }
                quickFix.setText(String.format(Common.OPTIONAL_THROW, variableName, mapText, throwText));
                holder.registerProblem(statement, Message.OPTIONAL_THROW, ProblemHighlightType.WARNING, quickFix);
            }
        };
    }

    private String getVariableName(PsiBinaryExpression binaryExpression) {
        if (binaryExpression.getOperationTokenType() == JavaTokenType.EQEQ) {
            PsiExpression leftOperand = binaryExpression.getLOperand();
            PsiExpression rightOperand = binaryExpression.getROperand();
            Predicate<PsiExpression> isNull = t -> (t instanceof PsiLiteralExpressionImpl && ((PsiLiteralExpressionImpl) t).getLiteralElementType() == JavaTokenType.NULL_KEYWORD);
            if (isNull.test(leftOperand) && null != rightOperand) {
                return rightOperand.getText();
            } else if (isNull.test(rightOperand)) {
                return leftOperand.getText();
            }
        }
        return null;
    }

    private String getExceptionText(PsiStatement thenStatement) {
        if (thenStatement instanceof PsiBlockStatement) {
            PsiBlockStatement blockStatement = (PsiBlockStatement) thenStatement;
            //获取处理代码块，且只有一个表达式
            PsiStatement[] statements = blockStatement.getCodeBlock().getStatements();
            if (statements.length != 1 || !(statements[0] instanceof PsiThrowStatement)) {
                return null;
            }
            String throwText = statements[0].getText().trim();
            if (throwText.length() > 6) {
                return throwText.substring(5, throwText.length() - 1).trim();
            }
        }
        return null;
    }

    private ReplaceQuickFix getQuickFix(PsiIfStatement statement) {
        PsiFile psiFile = statement.getContainingFile();
        ReplaceQuickFix quickFix = new ReplaceQuickFix(Message.OPTIONAL_THROW_FIX_NAME, statement, Common.BLANK_STRING);
        quickFix.addFixRunnable(() -> MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL));
        return quickFix;
    }

    private PsiElement getNextElement(PsiElement element) {
        PsiElement nextElement = element.getNextSibling();
        if (nextElement instanceof PsiWhiteSpace) {
            return getNextElement(nextElement);
        }
        return nextElement;
    }
}
