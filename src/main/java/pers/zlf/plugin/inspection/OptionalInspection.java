package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 10:18
 */
public class OptionalInspection extends AbstractBaseJavaLocalInspectionTool {
    /** 代码块的唯一语句 */
    private PsiStatement codeBlock;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                PsiExpression condition = ifStatement.getCondition();
                //校验并解析if语句
                PsiExpression judgmentObject = checkAndAnalysisIfStatement(ifStatement, condition);
                //判断的对象名
                String variableName = Optional.ofNullable(judgmentObject).map(PsiExpression::getText).orElse(null);
                if (StringUtil.isEmpty(variableName)) {
                    return;
                }
                BiConsumer<String, Integer> biConsumer = (textSuffix, simplifyType) -> {
                    if (textSuffix != null) {
                        holder.registerProblem(condition, Message.OPTIONAL, ProblemHighlightType.WARNING, new ReplaceQuickFix(variableName, textSuffix, simplifyType));
                    }
                };
                //简化 throw
                if (codeBlock instanceof PsiThrowStatement) {
                    biConsumer.accept(simplifyThrow((PsiThrowStatement) codeBlock), ReplaceQuickFix.SIMPLIFY_THROW);
                } else if (codeBlock instanceof PsiExpressionStatement) {
                    //简化赋值表达式
                    biConsumer.accept(simplifyExpression((PsiExpressionStatement) codeBlock, variableName), ReplaceQuickFix.SIMPLIFY_EXPRESSION);
                }
            }
        };
    }

    private PsiExpression checkAndAnalysisIfStatement(PsiIfStatement ifStatement, PsiExpression condition) {
        codeBlock = null;
        //二元表达式，单个if
        boolean simpleIfStatement = condition instanceof PsiBinaryExpression && ifStatement.getParent() instanceof PsiCodeBlock;
        if (!simpleIfStatement) {
            return null;
        }
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
        IElementType operationTokenType = binaryExpression.getOperationTokenType();
        PsiStatement elseStatement = ifStatement.getElseBranch();
        PsiStatement codeStatement;
        // 判断类型为 == 且 无else分支
        if (operationTokenType == JavaTokenType.EQEQ && elseStatement == null) {
            codeStatement = ifStatement.getThenBranch();
        } else if (operationTokenType == JavaTokenType.NE && elseStatement != null) {
            // 判断类型为 != 且 存在else分支
            codeStatement = elseStatement;
        } else {
            return null;
        }
        //代码块有且只有一个表达式
        if (codeStatement instanceof PsiBlockStatement) {
            PsiBlockStatement blockStatement = (PsiBlockStatement) codeStatement;
            codeBlock = Optional.of(blockStatement.getCodeBlock().getStatements()).filter(t -> t.length == 1).map(t -> t[0]).orElse(null);
            if (codeBlock == null) {
                return null;
            }
            return MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
        }
        return null;
    }

    private String simplifyThrow(PsiThrowStatement throwStatement) {
        String throwText = throwStatement.getText().trim();
        if (throwText.startsWith(Keyword.JAVA_THROW) && throwText.endsWith(Common.SEMICOLON)) {
            throwText = throwText.substring(5, throwText.length() - 1).trim();
            String finalThrowText = throwText;
            return String.format(Common.OPTIONAL_THROW, finalThrowText);
        }
        return null;
    }

    private String simplifyExpression(PsiExpressionStatement expressionStatement, String variableName) {
        PsiExpression expression = expressionStatement.getExpression();
        if (expression instanceof PsiAssignmentExpression) {
            PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) expression;
            PsiExpression leftExpression = assignmentExpression.getLExpression();
            //赋值表达式左边为引用
            if (leftExpression instanceof PsiReferenceExpression) {
                PsiReferenceExpression referenceExpression = (PsiReferenceExpression) leftExpression;
                String assignmentVariableName = referenceExpression.getReferenceName();
                if (!variableName.equals(assignmentVariableName)) {
                    return null;
                }
                //赋值表达式右边代码
                String rightText = Empty.of(assignmentExpression.getRExpression()).map(PsiExpression::getText).orElse(null);
                if (StringUtil.isNotEmpty(rightText)) {
                    return String.format(Common.OPTIONAL_ELSE, rightText);
                }
            }
        }
        return null;
    }

}
