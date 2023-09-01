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
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.pojo.SimplifyInfo;
import pers.zlf.plugin.pojo.psi.PsiDeclarationStatementModel;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 10:18
 */
public class OptionalInspection extends BaseInspection {
    /** if代码块的唯一语句 */
    private PsiStatement ifCode;
    /** if判断对象 */
    private PsiExpression judgmentObject;
    /** 简化信息 */
    private SimplifyInfo simplifyInfo;
    /** 替换代码的前缀 */
    private String replacePrefix;
    /** 判断对象名 */
    private String variableName;
    /** 判断对象的声明语句 */
    private String variableDeclaration;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement ifStatement) {
                //校验并解析if语句
                checkAndAnalysisIfStatement(ifStatement);
                if (ifCode == null || judgmentObject == null) {
                    return;
                }
                //快速解决方案
                ReplaceQuickFix quickFix = new ReplaceQuickFix(Message.OPTIONAL_FIX_NAME, ifStatement, Common.BLANK_STRING);
                PsiFile psiFile = ifStatement.getContainingFile();
                quickFix.addFixRunnable(() -> MyPsiUtil.importClass(psiFile, ClassType.OPTIONAL));
                //简化判断对象的声明
                simplifyDeclaration(quickFix);
                //简化 throw
                BiFunction<String, String, String> biFunction = null;
                if (ifCode instanceof PsiThrowStatement) {
                    biFunction = simplifyThrow(ifStatement, (PsiThrowStatement) ifCode);
                } else if (ifCode instanceof PsiExpressionStatement) {
                    biFunction = simplifyExpression(ifStatement, (PsiExpressionStatement) ifCode);
                }
                if (biFunction != null) {
                    //简化return
                    String simplifyText = Common.BLANK_STRING;
                    if (simplifyInfo != null && simplifyInfo.isSimplify()) {
                        PsiReturnStatement returnStatement = simplifyInfo.getReturnStatement();
                        quickFix.addFixRunnable(returnStatement::delete);
                        replacePrefix = Keyword.JAVA_RETURN + Common.SPACE;
                        simplifyText = simplifyInfo.getSimplifyText();
                    }
                    quickFix.setText(replacePrefix + biFunction.apply(Empty.of(variableDeclaration).orElse(variableName), simplifyText));
                    holder.registerProblem(ifStatement, Message.OPTIONAL, ProblemHighlightType.WARNING, quickFix);
                }
            }
        };
    }

    private void checkAndAnalysisIfStatement(PsiIfStatement ifStatement) {
        ifCode = null;
        judgmentObject = null;
        simplifyInfo = null;
        replacePrefix = Common.BLANK_STRING;
        variableDeclaration = Common.BLANK_STRING;
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
            ifCode = Optional.of(blockStatement.getCodeBlock().getStatements()).filter(t -> t.length == 1).map(t -> t[0]).orElse(null);
            if (ifCode == null) {
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
                variableName = Optional.ofNullable(judgmentObject).map(PsiExpression::getText).orElse(null);
            }
        }
    }

    private void simplifyDeclaration(ReplaceQuickFix quickFix) {
        PsiDeclarationStatementModel declarationModel = MyExpressionUtil.getDeclarationModel(judgmentObject);
        if (declarationModel != null) {
            quickFix.addFixRunnable(() -> declarationModel.getDeclaration().delete());
            variableDeclaration = declarationModel.getRightText();
            replacePrefix = declarationModel.getLeftText() + Common.EQ_STR;
        }
    }

    private BiFunction<String, String, String> simplifyThrow(PsiIfStatement ifStatement, PsiThrowStatement throwStatement) {
        String throwText = throwStatement.getText().trim();
        if (throwText.startsWith(Keyword.JAVA_THROW) && throwText.endsWith(Common.SEMICOLON)) {
            throwText = throwText.substring(5, throwText.length() - 1).trim();
            String finalThrowText = throwText;
            //简化return
            simplifyInfo = simplifyReturn(ifStatement, variableName);
            return (t, u) -> String.format(Common.OPTIONAL_THROW, t, u, finalThrowText);
        }
        return null;
    }

    private BiFunction<String, String, String> simplifyExpression(PsiIfStatement ifStatement, PsiExpressionStatement expressionStatement) {
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
                    if (StringUtil.isEmpty(replacePrefix)) {
                        replacePrefix = variableName + Common.EQ_STR;
                    }
                    //简化return
                    simplifyInfo = simplifyReturn(ifStatement, variableName);
                    if (simplifyInfo != null || StringUtil.isNotEmpty(variableDeclaration)) {
                        return (t, u) -> String.format(Common.OPTIONAL_ELSE, t, u, rightText);
                    }
                }
            }
        }
        return null;
    }

}
