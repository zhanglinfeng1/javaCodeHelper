package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 10:18
 */
public class OptionalInspection extends BaseInspection {
    /** 判断条件 */
    private PsiExpression condition;
    /** 代码块的唯一语句 */
    private PsiStatement codeBlock;
    /** 判断类型 */
    private IElementType operationTokenType;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                //快速解决方案
                ReplaceQuickFix quickFix = new ReplaceQuickFix(Message.OPTIONAL_FIX_NAME, ifStatement, Common.BLANK_STRING);
                //校验并解析if语句
                PsiExpression judgmentObject = checkAndAnalysisIfStatement(ifStatement, quickFix);
                //判断的对象名
                String variableName = Optional.ofNullable(judgmentObject).map(PsiExpression::getText).orElse(null);
                if (StringUtil.isEmpty(variableName)) {
                    return;
                }
                //替换文本BiFunction
                BiFunction<String, String, String> biFunction = null;
                //替换文本前缀
                String replacePrefix = Common.BLANK_STRING;
                BiPredicate<Boolean, Boolean> simplifyCheck = (t, u) -> false;
                //简化 throw
                if (codeBlock instanceof PsiThrowStatement) {
                    biFunction = simplifyThrow((PsiThrowStatement) codeBlock);
                    simplifyCheck = (t, u) -> true;
                } else if (operationTokenType == JavaTokenType.EQEQ && codeBlock instanceof PsiExpressionStatement) {
                    //判断类型为 == 、赋值表达式
                    biFunction = simplifyExpression((PsiExpressionStatement) codeBlock, variableName);
                    replacePrefix = variableName + Common.EQ_STR;
                    simplifyCheck = (t, u) -> t && u;
                }
                if (biFunction != null) {
                    PsiFile psiFile = ifStatement.getContainingFile();
                    quickFix.addFixRunnable(() -> MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL));
                    //简化判断对象的声明
                    simplifyDeclaration(judgmentObject);
                    //简化return
                    simplifyReturn(ifStatement, variableName);
                    if (!simplifyCheck.test(canSimplifyDeclaration, canSimplifyReturn)) {
                        return;
                    }
                    //简化声明
                    if (canSimplifyDeclaration) {
                        quickFix.addFixRunnable(declarationElement::delete);
                        replacePrefix = declarationLeftText + Common.EQ_STR;
                        variableName = declarationRightText;
                    }
                    //简化return
                    if (canSimplifyReturn && operationTokenType == JavaTokenType.EQEQ) {
                        quickFix.addFixRunnable(returnStatement::delete);
                        replacePrefix = Keyword.JAVA_RETURN + Common.SPACE;
                    }
                    quickFix.setText(replacePrefix + biFunction.apply(variableName, Optional.ofNullable(simplifyReturnText).orElse(Common.BLANK_STRING)));
                    holder.registerProblem(condition, Message.OPTIONAL, ProblemHighlightType.WARNING, quickFix);
                }
            }
        };
    }

    private PsiExpression checkAndAnalysisIfStatement(PsiIfStatement ifStatement, ReplaceQuickFix quickFix) {
        codeBlock = null;
        condition = ifStatement.getCondition();
        //二元表达式，单个if
        boolean simpleIfStatement = condition instanceof PsiBinaryExpression && ifStatement.getParent() instanceof PsiCodeBlock;
        if (!simpleIfStatement) {
            return null;
        }
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
        operationTokenType = binaryExpression.getOperationTokenType();
        PsiStatement thenStatement = ifStatement.getThenBranch();
        PsiStatement elseStatement = ifStatement.getElseBranch();
        PsiStatement codeStatement;
        // 判断类型为 == 且 无else分支
        if (operationTokenType == JavaTokenType.EQEQ && elseStatement == null) {
            codeStatement = thenStatement;
        } else if (operationTokenType == JavaTokenType.NE && elseStatement != null) {
            // 判断类型为 != 且 存在else分支
            codeStatement = elseStatement;
            if (thenStatement instanceof PsiBlockStatement) {
                PsiBlockStatement thenCodeBlock = (PsiBlockStatement) thenStatement;
                for (PsiElement element : thenCodeBlock.getCodeBlock().getChildren()) {
                    String text = element.getText();
                    if (StringUtil.isEmpty(text) || Common.LEFT_BRACE.equals(text) || Common.RIGHT_BRACE.equals(text)) {
                        continue;
                    }
                    quickFix.addPsiElement(element);
                }
            }
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
            PsiExpression leftOperand = binaryExpression.getLOperand();
            PsiExpression rightOperand = binaryExpression.getROperand();
            Predicate<PsiExpression> isNull = t -> (t instanceof PsiLiteralExpressionImpl && ((PsiLiteralExpressionImpl) t).getLiteralElementType() == JavaTokenType.NULL_KEYWORD);
            if (isNull.test(leftOperand)) {
                return rightOperand;
            } else if (isNull.test(rightOperand)) {
                return leftOperand;
            }
        }
        return null;
    }

    private BiFunction<String, String, String> simplifyThrow(PsiThrowStatement throwStatement) {
        String throwText = throwStatement.getText().trim();
        if (throwText.startsWith(Keyword.JAVA_THROW) && throwText.endsWith(Common.SEMICOLON)) {
            throwText = throwText.substring(5, throwText.length() - 1).trim();
            String finalThrowText = throwText;
            return (t, u) -> String.format(Common.OPTIONAL_THROW, t, u, finalThrowText);
        }
        return null;
    }

    private BiFunction<String, String, String> simplifyExpression(PsiExpressionStatement expressionStatement, String variableName) {
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
                    return (t, u) -> String.format(Common.OPTIONAL_ELSE, t, u, rightText);
                }
            }
        }
        return null;
    }

}
