package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

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
                if (condition instanceof PsiBinaryExpression && statement.getParent() instanceof PsiCodeBlock && statement.getElseBranch() == null) {
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
                    //获取处理结果
                    if (thenStatement instanceof PsiBlockStatement) {
                        PsiCodeBlock psiThenBlock = ((PsiBlockStatement) thenStatement).getCodeBlock();
                        PsiStatement[] statements = psiThenBlock.getStatements();
                        if (statements.length == 1 && statements[0] instanceof PsiThrowStatement) {
                            PsiThrowStatement throwStatement = (PsiThrowStatement) statements[0];
                            String text = throwStatement.getText();
                            text = text.substring(5, text.length() - 1).trim();
                            PsiFile psiFile = statement.getContainingFile();
                            holder.registerProblem(statement, Message.OPTIONAL_THROW, ProblemHighlightType.WARNING, getQuickFix(psiFile, statement, String.format(Common.OPTIONAL_THROW, variableName, text)));
                        }
                    }
                }
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

    private LocalQuickFix getQuickFix(PsiFile psiFile, PsiIfStatement statement, String text) {
        return new LocalQuickFix() {
            @NotNull
            @Override
            public String getName() {
                return Message.OPTIONAL_THROW_FIX_NAME;
            }

            @NotNull
            @Override
            public String getFamilyName() {
                return getName();
            }

            @Override
            public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                CommentTracker commentTracker = new CommentTracker();
                commentTracker.replaceAndRestoreComments(statement, text);
                if (psiFile instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                    MyPsiUtil.findClassByFullName(javaFile.getResolveScope(), ClassType.OPTIONAL).ifPresent(javaFile::importClass);
                }
            }
        };
    }

}
