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
import com.intellij.psi.PsiConditionalExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceIfQuickFix;
import pers.zlf.plugin.inspection.fix.ReplaceTernaryExpressionQuickFix;
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
    /** 判断类型 */
    private IElementType operationTokenType;
    /** 可以简化throw */
    private boolean simplifyThrow;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(@NotNull PsiIfStatement ifStatement) {
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
                        holder.registerProblem(condition, Message.OPTIONAL, ProblemHighlightType.WARNING, new ReplaceIfQuickFix(variableName, textSuffix, simplifyType));
                    }
                };
                //简化 throw
                if (codeBlock instanceof PsiThrowStatement && simplifyThrow) {
                    biConsumer.accept(simplifyThrow((PsiThrowStatement) codeBlock), ReplaceIfQuickFix.SIMPLIFY_THROW);
                } else if (operationTokenType == JavaTokenType.EQEQ && codeBlock instanceof PsiExpressionStatement) {
                    //简化赋值表达式
                    biConsumer.accept(simplifyExpression((PsiExpressionStatement) codeBlock, variableName), ReplaceIfQuickFix.SIMPLIFY_EXPRESSION);
                } else if (operationTokenType == JavaTokenType.NE) {
                    //简化方法调用
                    biConsumer.accept(simplifyMethodCall(judgmentObject), ReplaceIfQuickFix.SIMPLIFY_IF_PRESENT);
                }
            }

            @Override
            public void visitConditionalExpression(@NotNull PsiConditionalExpression conditionalExpression) {
                //三元表达式
                if (conditionalExpression.getParent() instanceof PsiLocalVariable && conditionalExpression.getCondition() instanceof PsiBinaryExpression binaryExpression) {
                    IElementType tokenType = binaryExpression.getOperationTokenType();
                    String nullText;
                    String notNullText;
                    if (tokenType == JavaTokenType.EQEQ) {
                        nullText = Optional.ofNullable(conditionalExpression.getThenExpression()).map(PsiExpression::getText).orElse(null);
                        notNullText = Optional.ofNullable(conditionalExpression.getElseExpression()).map(PsiExpression::getText).orElse(null);
                    } else if (tokenType == JavaTokenType.NE) {
                        notNullText = Optional.ofNullable(conditionalExpression.getThenExpression()).map(PsiExpression::getText).orElse(null);
                        nullText = Optional.ofNullable(conditionalExpression.getElseExpression()).map(PsiExpression::getText).orElse(null);
                    } else {
                        return;
                    }
                    PsiExpression expression = MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
                    if (expression == null || StringUtil.isEmpty(nullText) || StringUtil.isEmpty(notNullText)) {
                        return;
                    }
                    String variable = expression.getText();
                    String replaceText = String.format(Common.OPTIONAL, variable);
                    if (!variable.equals(notNullText)) {
                        replaceText = replaceText + Common.DOT + Common.MAP_STR + String.format(Common.LAMBDA_STR, Common.T, notNullText);
                    }
                    replaceText = replaceText + String.format(Common.OPTIONAL_ELSE, nullText);
                    holder.registerProblem(conditionalExpression, Message.OPTIONAL, ProblemHighlightType.WARNING, new ReplaceTernaryExpressionQuickFix(replaceText));
                }
            }
        };
    }

    private PsiExpression checkAndAnalysisIfStatement(PsiIfStatement ifStatement, PsiExpression condition) {
        codeBlock = null;
        simplifyThrow = true;
        //二元表达式，单个if
        boolean simpleIfStatement = condition instanceof PsiBinaryExpression && ifStatement.getParent() instanceof PsiCodeBlock;
        if (!simpleIfStatement) {
            return null;
        }
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
        operationTokenType = binaryExpression.getOperationTokenType();
        PsiStatement elseStatement = ifStatement.getElseBranch();
        PsiStatement codeStatement;
        // 判断类型为 == 且 无else分支
        if (operationTokenType == JavaTokenType.EQEQ && elseStatement == null) {
            codeStatement = ifStatement.getThenBranch();
        } else if (operationTokenType == JavaTokenType.NE && elseStatement != null) {
            // 判断类型为 != 且 存在else分支
            codeStatement = elseStatement;
        } else if (operationTokenType == JavaTokenType.NE) {
            // 判断类型为 != 且 无else分支
            codeStatement = ifStatement.getThenBranch();
            simplifyThrow = false;
        } else {
            return null;
        }
        //代码块有且只有一个表达式
        if (codeStatement instanceof PsiBlockStatement blockStatement) {
            codeBlock = Optional.of(blockStatement.getCodeBlock().getStatements()).filter(t -> t.length == 1).map(t -> t[0]).orElse(null);
            if (codeBlock == null) {
                return null;
            }
            return MyExpressionUtil.getExpressionComparedToNull(binaryExpression);
        }
        return null;
    }

    private String simplifyThrow(PsiThrowStatement throwStatement) {
        String throwText = Optional.ofNullable(throwStatement).map(PsiThrowStatement::getText).map(String::trim).orElse(Common.BLANK_STRING);
        if (throwText.startsWith(Keyword.JAVA_THROW) && throwText.endsWith(Common.SEMICOLON)) {
            throwText = throwText.substring(5, throwText.length() - 1).trim();
            String finalThrowText = throwText;
            return String.format(Common.OPTIONAL_THROW, finalThrowText);
        }
        return null;
    }

    private String simplifyExpression(PsiExpressionStatement expressionStatement, String variableName) {
        PsiExpression expression = expressionStatement.getExpression();
        if (expression instanceof PsiAssignmentExpression assignmentExpression) {
            PsiExpression leftExpression = assignmentExpression.getLExpression();
            //赋值表达式左边为引用
            if (leftExpression instanceof PsiReferenceExpression referenceExpression) {
                String assignmentVariableName = referenceExpression.getReferenceName();
                if (!variableName.equals(assignmentVariableName)) {
                    return null;
                }
                //赋值表达式右边代码
                String rightText = Empty.of(assignmentExpression.getRExpression()).map(PsiExpression::getText).orElse(null);
                if (StringUtil.isNotEmpty(rightText)) {
                    return String.format(Common.OPTIONAL_ELSE, rightText) + Common.SEMICOLON;
                }
            }
        }
        return null;
    }

    private String simplifyMethodCall(PsiExpression judgmentObject) {
        if (codeBlock instanceof PsiExpressionStatementImpl expressionStatement && expressionStatement.getExpression() instanceof PsiMethodCallExpressionImpl methodCallExpression) {
            String variableName = judgmentObject.getText();
            if (StringUtil.isEmpty(variableName)) {
                return null;
            }
            String simplifyText = MyExpressionUtil.simplifyMethodCall(methodCallExpression, variableName);
            if (StringUtil.isNotEmpty(simplifyText)) {
                simplifyText = Common.IF_PRESENT_STR + simplifyText + Common.SEMICOLON;
            }
            return simplifyText;
        }
        return null;
    }
}
