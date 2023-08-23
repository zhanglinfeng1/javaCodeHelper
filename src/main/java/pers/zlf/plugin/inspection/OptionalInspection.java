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
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReferenceExpression;
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
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyExpressionUtil;
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
                    PsiReturnStatement returnStatement = (PsiReturnStatement) nextElement;
                    mapText = simplifyReturn(returnStatement, variableName);
                    if (StringUtil.isNotEmpty(mapText)) {
                        quickFix.addFixRunnable(returnStatement::delete);
                    }
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

    private String simplifyReturn(PsiReturnStatement returnStatement, String variableName) {
        PsiExpression expression = returnStatement.getReturnValue();
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        //返回单个变量或常量
        if (expression instanceof PsiReferenceExpression || expression instanceof PsiLiteralExpression) {
            return variableName.equals(elementText) ? Common.BLANK_STRING : String.format(Common.MAP_COMMON_STR, elementText);
        } else if (expression instanceof PsiNewExpression) {
            //返回 new 对象
            PsiNewExpression newExpression = (PsiNewExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(newExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                return Optional.ofNullable(newExpression.getClassReference())
                        .map(PsiJavaCodeReferenceElement::getReferenceName)
                        .map(t -> String.format(Common.MAP_LAMBDA_STR, t, Keyword.JAVA_NEW.toUpperCase()))
                        .orElse(Common.BLANK_STRING);
            }
        } else if (expression instanceof PsiMethodCallExpression) {
            //调用方法
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(methodCallExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                String[] expressionTextArr = referenceExpression.getQualifiedName().split(Regex.DOT);
                if (expressionTextArr.length == 1) {
                    return String.format(Common.MAP_LAMBDA_STR, Keyword.JAVA_THIS, expressionTextArr[0]);
                } else if (expressionTextArr.length == 2) {
                    return String.format(Common.MAP_LAMBDA_STR, expressionTextArr[0], expressionTextArr[1]);
                }
            }
        }
        return Common.BLANK_STRING;
    }

}
