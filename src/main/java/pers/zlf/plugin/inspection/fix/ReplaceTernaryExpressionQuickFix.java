package pers.zlf.plugin.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiConditionalExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.MyPsiUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/12/18 14:39
 */
public class ReplaceTernaryExpressionQuickFix implements LocalQuickFix {
    /** 替换文本 */
    private final String replaceText;

    public ReplaceTernaryExpressionQuickFix(String replaceText) {
        this.replaceText = replaceText;
    }

    @NotNull
    @Override
    public String getName() {
        return Message.OPTIONAL_FIX_NAME;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiConditionalExpression conditionalExpression = (PsiConditionalExpression) descriptor.getPsiElement();
        PsiFile psiFile = conditionalExpression.getContainingFile();
        //替换
        PsiElement newElement = new CommentTracker().replaceAndRestoreComments(conditionalExpression, replaceText);
        CodeStyleManager.getInstance(project).reformat(newElement);
        //导入java.util.Optional
        MyPsiUtil.importClass(psiFile, CommonClassNames.JAVA_UTIL_OPTIONAL);
    }

}
