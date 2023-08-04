package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/4 15:21
 */
public abstract class BaseCompletionContributor extends CompletionContributor {
    /** 待补全元素 */
    protected CompletionParameters parameters;
    /** 补全结果 */
    protected CompletionResultSet result;
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
        this.parameters = parameters;
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

    protected void addCompletionResult(LookupElementBuilder builder) {
        result.addElement(builder.withIcon(Icon.LOGO).withCaseSensitivity(true));
    }
}
