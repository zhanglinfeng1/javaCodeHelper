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
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaToken;
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
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
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
                PsiStatement thenStatement = statement.getThenBranch();
                //二元表达式，单个if，无else分支
                boolean simpleIfStatement = condition instanceof PsiBinaryExpression && statement.getParent() instanceof PsiCodeBlock
                        && statement.getElseBranch() == null && thenStatement instanceof PsiBlockStatement;
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
                //获取处理代码块，且只有一个表达式
                PsiStatement[] statements = ((PsiBlockStatement) thenStatement).getCodeBlock().getStatements();
                if (statements.length != 1 || !(statements[0] instanceof PsiThrowStatement)) {
                    return;
                }
                String throwText = statements[0].getText().trim();
                if (throwText.length() < 6) {
                    return;
                }
                throwText = throwText.substring(5, throwText.length() - 1).trim();
                ReplaceQuickFix quickFix = getQuickFix(statement, variableName, throwText);
                PsiElement nextElement = getNextElement(statement);
                //简化return
                if (nextElement instanceof PsiReturnStatement) {
                    simplifyReturn((PsiReturnStatement) nextElement, quickFix, variableName, throwText);
                }
                holder.registerProblem(statement, Message.OPTIONAL_THROW, ProblemHighlightType.WARNING, quickFix);
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

    private ReplaceQuickFix getQuickFix(PsiIfStatement statement, String variableName, String throwText) {
        String replaceText = String.format(Common.OPTIONAL_THROW, variableName, throwText);
        PsiFile psiFile = statement.getContainingFile();
        ReplaceQuickFix quickFix = new ReplaceQuickFix(Message.OPTIONAL_THROW_FIX_NAME, statement, replaceText);
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

    private void simplifyReturn(PsiReturnStatement returnStatement, ReplaceQuickFix quickFix, String variableName, String throwText) {
        PsiElement element = getChildrenElement(returnStatement.getChildren());
        String elementText = Optional.ofNullable(element).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        String replaceText = Common.BLANK_STRING;
        if (element instanceof PsiReferenceExpression || element instanceof PsiLiteralExpression) {
            replaceText = variableName.equals(elementText) ? quickFix.getText() : String.format(Common.OPTIONAL_MAP_THROW, variableName, Common.LAMBDA_FILL_STR + elementText, throwText);
        } else if (element instanceof PsiNewExpression) {
            PsiNewExpression newExpression = (PsiNewExpression) element;
            String parameterName = Optional.ofNullable(newExpression.getArgumentList())
                    .map(PsiExpressionList::getExpressions)
                    .filter(t -> t.length == 1)
                    .map(t -> t[0].getText())
                    .orElse(Common.BLANK_STRING);
            if (parameterName.equals(variableName)) {
                replaceText = Optional.ofNullable(newExpression.getClassReference())
                        .map(PsiJavaCodeReferenceElement::getReferenceName)
                        .map(t -> String.format(Common.OPTIONAL_MAP_THROW, variableName, t + Common.DOUBLE_COLON + Keyword.JAVA_NEW.toUpperCase(), throwText))
                        .orElse(Common.BLANK_STRING);
            }
        } else if (element instanceof PsiMethodCallExpression) {
            //TODO lambda简化
        }
        if (StringUtil.isNotEmpty(replaceText)) {
            quickFix.setText(Keyword.JAVA_RETURN + Common.SPACE + replaceText);
            quickFix.addFixRunnable(returnStatement::delete);
        }
    }

    private PsiElement getChildrenElement(PsiElement[] elements) {
        List<PsiElement> elementList = new ArrayList<>();
        for (PsiElement element : elements) {
            if (element instanceof PsiWhiteSpace || element instanceof PsiJavaToken) {
                continue;
            }
            elementList.add(element);
        }
        return elementList.size() != 1 ? null : elementList.get(0);
    }
}
