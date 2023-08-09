package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/4 15:21
 */
public abstract class BaseCompletionContributor extends CompletionContributor {
    /** 原始位置元素 */
    protected PsiElement originalPositionElement;
    /** 补全结果 */
    private CompletionResultSet result;
    /** 当前元素 */
    protected PsiElement currentElement;
    /** 当前文本 */
    protected String currentText;
    @Override
    public final void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        //未开启代码补全
        boolean enableCodeCompletion = ConfigFactory.getInstance().getCommonConfig().isEnableCodeCompletion();
        if (!enableCodeCompletion) {
            return;
        }
        this.originalPositionElement = parameters.getOriginalPosition();
        this.result = result;
        this.currentElement = parameters.getPosition();
        this.currentText = currentElement.getText().replace(ClassType.INTELLIJ_IDEA_RULEZZZ, Common.BLANK_STRING);
        if (StringUtil.isNotEmpty(currentText) && check()) {
            this.completion();
        }
    }

    /**
     * 校验逻辑
     */
    protected abstract boolean check();

    /**
     * 具体补全逻辑
     */
    protected abstract void completion();

    protected void addCompletionResult(String completionText) {
        addCompletionResult(completionText, completionText, null);
    }

    protected void addCompletionResult(String completionText, String presentableText, InsertHandler<LookupElement> insertHandler) {
        LookupElementBuilder builder = LookupElementBuilder.create(completionText).withPresentableText(presentableText).withIcon(Icon.LOGO).withCaseSensitivity(true);
        result.addElement(Optional.ofNullable(insertHandler).map(builder::withInsertHandler).orElse(builder));
    }
}
