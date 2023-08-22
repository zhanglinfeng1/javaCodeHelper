package pers.zlf.plugin.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/18 17:49
 */
public class ReplaceQuickFix implements LocalQuickFix {
    private final String name;
    private final PsiElement psiElement;
    private final String text;
    private final Runnable runnable;

    public ReplaceQuickFix(String name, PsiElement psiElement, String text, Runnable runnable) {
        this.text = text;
        this.psiElement = psiElement;
        this.name = name;
        this.runnable = runnable;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        CommentTracker commentTracker = new CommentTracker();
        PsiElement newElement = commentTracker.replaceAndRestoreComments(psiElement, text);
        CodeStyleManager.getInstance(project).reformat(newElement);
        Optional.ofNullable(runnable).ifPresent(Runnable::run);
    }
}
