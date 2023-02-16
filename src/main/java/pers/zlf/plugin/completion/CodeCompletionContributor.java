package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.completion.service.Completion;
import pers.zlf.plugin.completion.service.impl.ConstructorCompletion;
import pers.zlf.plugin.completion.service.impl.MethodCompletion;
import pers.zlf.plugin.constant.ICON;

import java.util.Optional;

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
        //当前光标所在的方法
        Optional.ofNullable(PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), PsiMethod.class)).ifPresent(method -> {
            Completion completion = method.isConstructor() ? new ConstructorCompletion(method, parameters.getPosition()) : new MethodCompletion(method, parameters.getPosition());
            completion.getLookupElement().forEach(e -> result.addElement(e.withIcon(ICON.LOGO).withCaseSensitivity(true)));
        });
    }
}
