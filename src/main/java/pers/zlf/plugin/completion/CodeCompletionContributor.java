package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.completion.code.BaseCompletion;
import pers.zlf.plugin.completion.code.ConstructorCompletion;
import pers.zlf.plugin.completion.code.MethodCompletion;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.factory.ConfigFactory;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 14:18
 */
public class CodeCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        //未开启代码补全
        boolean enableCodeCompletion = ConfigFactory.getInstance().getCommonConfig().isEnableCodeCompletion();
        if (!enableCodeCompletion) {
            return;
        }
        //当前光标所在的方法
        Optional.ofNullable(PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), PsiMethod.class)).ifPresent(method -> {
            BaseCompletion baseCompletion = method.isConstructor() ? new ConstructorCompletion(method, parameters.getPosition()) : new MethodCompletion(method, parameters.getPosition());
            baseCompletion.getLookupElement().forEach(e -> result.addElement(e.withIcon(Icon.LOGO).withCaseSensitivity(true)));
        });
    }
}
