package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/5/9 9:30
 */
public class FeignMethodInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass psiClass) {
                if (!MyPsiUtil.isFeign(psiClass)) {
                    return;
                }
                for (PsiMethod method : psiClass.getMethods()) {
                    Optional.ofNullable(method.getNameIdentifier()).ifPresent(identifier -> {
                        PsiReference[] references = ReferencesSearch.search(method).toArray(new PsiReference[0]);
                        if (references.length == 0) {
                            holder.registerProblem(identifier, String.format(Message.UNUSED_METHOD, method.getName()), ProblemHighlightType.LIKE_UNUSED_SYMBOL);
                        }
                    });
                }
            }
        };
    }

}

