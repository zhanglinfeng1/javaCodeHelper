package pers.zlf.plugin.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.siyeh.ig.psiutils.CommentTracker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/18 17:49
 */
public class ReplaceQuickFix implements LocalQuickFix {
    /** 提示信息 */
    private final String name;
    /** 被替换元素 TODO 内存泄漏 */
    private final PsiElement psiElement;
    /** 替换代码 */
    private String text;
    /** 执行方法 */
    private final List<Runnable> runnableList;

    public ReplaceQuickFix(String name, PsiElement psiElement, String text) {
        this.text = text;
        this.psiElement = psiElement;
        this.name = name;
        this.runnableList = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void addFixRunnable(Runnable runnable) {
        this.runnableList.add(runnable);
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
        runnableList.forEach(Runnable::run);
    }
}
