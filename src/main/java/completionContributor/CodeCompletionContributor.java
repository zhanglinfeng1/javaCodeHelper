package completionContributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import completionContributor.impl.ConstructorCompletion;
import completionContributor.impl.MethodCompletion;
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        PsiElement psiElement = parameters.getOriginalPosition();
        //当前光标前的字符串
        if (COMMON_CONSTANT.DOT.equals(psiElement.getText())) {
            return;
        }
        //当前光标所在的方法
        PsiMethod currentMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
        if (null == currentMethod) {
            return;
        }
        List<LookupElementBuilder> elementList;
        BasicCompletion basicCompletion;
        if (currentMethod.isConstructor()) {
            basicCompletion = new ConstructorCompletion(currentMethod);
        } else {
            basicCompletion = new MethodCompletion(currentMethod);
        }
        elementList = basicCompletion.getLookupElement();
        if (elementList.isEmpty()) {
            return;
        }
        elementList.forEach(e -> result.addElement(e.withIcon(ICON_CONSTANT.BO_LUO_SVG_16).withCaseSensitivity(true)));
    }

}
