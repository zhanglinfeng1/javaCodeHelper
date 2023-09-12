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
import java.util.ListIterator;

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
    /** 待添加的元素 */
    private final List<PsiElement> addList;

    public ReplaceQuickFix(String name, PsiElement psiElement, String text) {
        this.text = text;
        this.psiElement = psiElement;
        this.name = name;
        this.runnableList = new ArrayList<>();
        this.addList = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addPsiElement(PsiElement psiElement) {
        this.addList.add(psiElement);
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
        PsiElement parentElement = psiElement.getParent();
        //添加元素
        ListIterator<PsiElement> iterator = addList.listIterator(addList.size());
        while (iterator.hasPrevious()) {
            PsiElement element = iterator.previous();
            parentElement.addAfter(element, psiElement);
            CodeStyleManager.getInstance(project).reformat(element);
        }
        //替换
        PsiElement newElement = new CommentTracker().replaceAndRestoreComments(psiElement, text);
        CodeStyleManager.getInstance(project).reformat(newElement);
        //其他处理步骤
        runnableList.forEach(Runnable::run);
    }
}
