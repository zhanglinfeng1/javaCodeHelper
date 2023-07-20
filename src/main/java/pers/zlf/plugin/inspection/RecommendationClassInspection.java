package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2023/5/9 9:30
 */
public class RecommendationClassInspection extends AbstractBaseJavaLocalInspectionTool {
    private final Map<String, String> CLASS_MAP = new HashMap<>() {{
        put("java.sql.Timestamp", "LocalDateTime");
        put("java.sql.Date", "LocalDate");
        put("java.sql.Time", "LocalTime");
        put("java.util.Date", "LocalDateTime");
    }};

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                checkType(holder, field.getTypeElement(), field.getType());
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                checkType(holder, variable.getTypeElement(), variable.getType());
            }

            @Override
            public void visitParameter(PsiParameter parameter) {
                super.visitParameter(parameter);
                checkType(holder, parameter.getTypeElement(), parameter.getType());
            }
        };
    }

    private void checkType(ProblemsHolder holder, PsiElement psiElement, PsiType psiType) {
        Empty.of(psiElement).map(t -> psiType.getInternalCanonicalText()).map(CLASS_MAP::get).ifPresent(t -> holder.registerProblem(psiElement, Message.SUGGESTED_USE + t));
    }
}

