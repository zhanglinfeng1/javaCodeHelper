package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
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
    /** 当前元素 */
    protected PsiElement currentElement;
    /** 当前文本 */
    protected String currentText;
    /** 补全参数 */
    protected CompletionParameters parameters;
    /** 补全结果 */
    private CompletionResultSet result;

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
        this.currentElement = parameters.getPosition();
        this.currentText = currentElement.getText();
        if (this.currentText.startsWith(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
            this.currentText = Common.BLANK_STRING;
        } else if (this.currentText.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
            this.currentText = this.currentText.split(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)[0];
        }
        this.parameters = parameters;
        if (StringUtil.isNotEmpty(currentText) && check()) {
            this.result = result;
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
        addCompletionResult(completionText, completionText);
    }

    protected void addCompletionResult(String completionText, String presentableText) {
        if (completionText.endsWith(Common.SEMICOLON)) {
            PsiElement next = PsiTreeUtil.nextVisibleLeaf(this.currentElement);
            if (null != next && Common.SEMICOLON.equals(next.getText())) {
                completionText = completionText.substring(0, completionText.length() - 1);
            }
        }
        addCompletionResult(completionText, presentableText, null);
    }

    protected void addCompletionResult(String completionText, String presentableText, InsertHandler<LookupElement> insertHandler) {
        LookupElementBuilder builder = LookupElementBuilder.create(completionText).withPresentableText(presentableText).withIcon(Icon.LOGO).withCaseSensitivity(true);
        result.addElement(Optional.ofNullable(insertHandler).map(builder::withInsertHandler).orElse(builder));
    }
}
