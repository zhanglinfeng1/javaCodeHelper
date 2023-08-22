package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
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
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 10:18
 */
public class OptionalInspection extends AbstractBaseJavaLocalInspectionTool {
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
                PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
                if (binaryExpression.getOperationTokenType() != JavaTokenType.EQEQ) {
                    return;
                }
                //获取变量名
                String variableName = getVariableName(binaryExpression);
                if (StringUtil.isEmpty(variableName)) {
                    return;
                }
                PsiStatement thenStatement = statement.getThenBranch();
                if (!(thenStatement instanceof PsiBlockStatement)) {
                    return;
                }
                //获取处理代码块，且只有一个表达式
                PsiCodeBlock psiThenBlock = ((PsiBlockStatement) thenStatement).getCodeBlock();
                PsiStatement[] statements = psiThenBlock.getStatements();
                if (statements.length != 1 || !(statements[0] instanceof PsiThrowStatement)) {
                    return;
                }
                String replaceText = getReplaceText((PsiThrowStatement) statements[0], variableName);
                PsiFile psiFile = statement.getContainingFile();
                Runnable runnable = () -> MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL);
                PsiElement nextElement = getNextElement(statement);
                //简化return
                if (nextElement instanceof PsiReturnStatement) {
                    PsiReturnStatement returnStatement = (PsiReturnStatement) nextElement;
                    String returnValue = Optional.ofNullable(returnStatement.getReturnValue()).map(PsiExpression::getText).orElse(Common.BLANK_STRING);
                    if (returnValue.equals(variableName)) {
                        replaceText = Keyword.JAVA_RETURN + Common.SPACE + replaceText;
                        runnable = () -> {
                            returnStatement.delete();
                            MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL);
                        };
                    }
                }
                holder.registerProblem(statement, Message.OPTIONAL_THROW, ProblemHighlightType.WARNING, new ReplaceQuickFix(Message.OPTIONAL_THROW_FIX_NAME, statement, replaceText, runnable));
            }
        };
    }

    private String getVariableName(PsiBinaryExpression binaryExpression) {
        PsiExpression leftOperand = binaryExpression.getLOperand();
        PsiExpression rightOperand = binaryExpression.getROperand();
        Predicate<PsiExpression> isNull = t -> (t instanceof PsiLiteralExpressionImpl && ((PsiLiteralExpressionImpl) t).getLiteralElementType() == JavaTokenType.NULL_KEYWORD);
        if (isNull.test(leftOperand) && null != rightOperand) {
            return rightOperand.getText();
        } else if (isNull.test(rightOperand)) {
            return leftOperand.getText();
        }
        return null;
    }

    private String getReplaceText(PsiThrowStatement throwStatement, String variableName) {
        String throwText = throwStatement.getText().trim();
        throwText = throwText.substring(5, throwText.length() - 1).trim();
        return String.format(Common.OPTIONAL_THROW, variableName, throwText);
    }

    private PsiElement getNextElement(PsiElement element) {
        PsiElement nextElement = element.getNextSibling();
        if (nextElement instanceof PsiWhiteSpace) {
            return getNextElement(nextElement);
        }
        return nextElement;
    }
}
