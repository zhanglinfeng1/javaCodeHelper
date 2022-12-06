package inspectionTool;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiType;
import constant.TYPE;
import org.jetbrains.annotations.NotNull;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/12/5 14:10
 */
public class CodeInspectionTool extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                checkType(holder, field, field.getType());
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                checkType(holder, variable, variable.getType());
            }
        };
    }

    private void checkType(ProblemsHolder holder, PsiElement psiElement, PsiType psiType) {
        String typeFullName = psiType.getInternalCanonicalText();
        switch (typeFullName) {
            case TYPE.DATE:
                holder.registerProblem(psiElement, "Replace with LocalDateTime");
                return;
            case TYPE.SIMPLE_DATE_FORMAT:
                holder.registerProblem(psiElement, "Replace with DateTimeFormatter");
                return;
            default:
        }
    }
}
