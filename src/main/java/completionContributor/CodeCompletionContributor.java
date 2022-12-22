package completionContributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import constant.ICON;
import org.jetbrains.annotations.NotNull;
import service.Completion;
import service.impl.ConstructorCompletion;
import service.impl.MethodCompletion;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/14 14:18
 */
public class CodeCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement psiElement = parameters.getPosition();
        //当前光标所在的方法
        PsiMethod currentMethod = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), PsiMethod.class);
        if (null == currentMethod) {
            return;
        }
        Completion completion;
        if (currentMethod.isConstructor()) {
            completion = new ConstructorCompletion(currentMethod, psiElement);
        } else {
            completion = new MethodCompletion(currentMethod, psiElement);
        }
        completion.getLookupElement().forEach(e -> result.addElement(e.withIcon(ICON.BO_LUO_SVG_16).withCaseSensitivity(true)));
    }
}
