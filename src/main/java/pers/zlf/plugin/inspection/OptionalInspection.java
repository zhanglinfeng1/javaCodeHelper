package pers.zlf.plugin.inspection;

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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.pojo.psi.PsiDeclarationStatementModel;
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
    /** if代码块的唯一语句 */
    private PsiStatement statement;
    /** if判断对象 */
    private PsiExpression judgmentObject;
    /** 替换的代码 */
    private BiFunction<String, String, String> biFunction;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                //校验并解析if语句
                checkAndAnalysisIfStatement(ifStatement);
                if (statement == null || judgmentObject == null) {
                    return;
                }
                //获取判断对象
                String variableName = judgmentObject.getText();
                biFunction = null;
                //获取判断对象的声明信息
                PsiDeclarationStatementModel declarationModel = getDeclarationModel(judgmentObject);
                BiPredicate<Boolean, String> biPredicate = (t, u) -> false;
                String prefix = Common.BLANK_STRING;
                //简化 throw
                if (statement instanceof PsiThrowStatement) {
                    simplifyThrow((PsiThrowStatement) statement);
                    biPredicate = (t, u) -> t;
                } else if (statement instanceof PsiExpressionStatement) {
                    //简化赋值
                    simplifyExpression((PsiExpressionStatement) statement, variableName);
                    biPredicate = (t, u) -> t && StringUtil.isEmpty(u);
                    prefix = variableName + Common.EQ_STR;
                }
                if (biFunction != null) {
                    //快速解决方案
                    ReplaceQuickFix quickFix = new ReplaceQuickFix(Message.OPTIONAL_FIX_NAME, ifStatement, Common.BLANK_STRING);
                    PsiFile psiFile = ifStatement.getContainingFile();
                    quickFix.addFixRunnable(() -> MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL));
                    //简化return
                    simplifyReturn(ifStatement, variableName);
                    //简化声明
                    String mapText = Common.BLANK_STRING;
                    if (declarationModel != null) {
                        quickFix.addFixRunnable(declaration::delete);
                        variableName = declarationModel.getRightText();
                        prefix = declarationModel.getLeftText() + Common.EQ_STR;
                    }
                    //简化return
                    if (biPredicate.test(isSimplify, simplifyText)) {
                        quickFix.addFixRunnable(returnStatement::delete);
                        mapText = simplifyText;
                        prefix = Keyword.JAVA_RETURN + Common.SPACE;
                    }
                    String replaceText = biFunction.apply(variableName, mapText);
                    quickFix.setText(prefix + replaceText);
                    holder.registerProblem(ifStatement, Message.OPTIONAL, ProblemHighlightType.WARNING, quickFix);
                }
            }
        };
    }

    private void checkAndAnalysisIfStatement(PsiIfStatement ifStatement) {
        PsiExpression condition = ifStatement.getCondition();
        //二元表达式，单个if，无else分支
        boolean simpleIfStatement = condition instanceof PsiBinaryExpression && ifStatement.getParent() instanceof PsiCodeBlock && ifStatement.getElseBranch() == null;
        if (!simpleIfStatement) {
            return;
        }
        //获取if代码块，且只有一个表达式
        PsiStatement thenStatement = ifStatement.getThenBranch();
        if (thenStatement instanceof PsiBlockStatement) {
            PsiBlockStatement blockStatement = (PsiBlockStatement) thenStatement;
            statement = Optional.of(blockStatement.getCodeBlock().getStatements()).filter(t -> t.length == 1).map(t -> t[0]).orElse(null);
            if (statement == null) {
                return;
            }
            PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
            if (binaryExpression.getOperationTokenType() == JavaTokenType.EQEQ) {
                PsiExpression leftOperand = binaryExpression.getLOperand();
                PsiExpression rightOperand = binaryExpression.getROperand();
                Predicate<PsiExpression> isNull = t -> (t instanceof PsiLiteralExpressionImpl && ((PsiLiteralExpressionImpl) t).getLiteralElementType() == JavaTokenType.NULL_KEYWORD);
                if (isNull.test(leftOperand)) {
                    judgmentObject = rightOperand;
                } else if (isNull.test(rightOperand)) {
                    judgmentObject = leftOperand;
                }
            }
        }
    }

    private void simplifyThrow(PsiThrowStatement throwStatement) {
        String throwText = throwStatement.getText().trim();
        if (throwText.startsWith(Keyword.JAVA_THROW) && throwText.endsWith(Common.SEMICOLON)) {
            throwText = throwText.substring(5, throwText.length() - 1).trim();
            String finalThrowText = throwText;
            biFunction = (t, u) -> String.format(Common.OPTIONAL_THROW, t, u, finalThrowText);
        }
    }

    private void simplifyExpression(PsiExpressionStatement expressionStatement, String variableName) {
        PsiExpression expression = expressionStatement.getExpression();
        if (!(expression instanceof PsiAssignmentExpression)) {
            return;
        }
        PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) expression;
        PsiExpression leftExpression = assignmentExpression.getLExpression();
        //赋值表达式左边为引用
        if (leftExpression instanceof PsiReferenceExpression) {
            PsiReferenceExpression referenceExpression = (PsiReferenceExpression) leftExpression;
            String assignmentVariableName = referenceExpression.getReferenceName();
            if (!variableName.equals(assignmentVariableName)) {
                return;
            }
            //赋值表达式左边为引用
            String rightText = Empty.of(assignmentExpression.getRExpression()).map(PsiExpression::getText).orElse(null);
            if (StringUtil.isNotEmpty(rightText)) {
                biFunction = (t, u) -> String.format(Common.OPTIONAL_ELSE, t, u, rightText);
            }
        }
    }

}
