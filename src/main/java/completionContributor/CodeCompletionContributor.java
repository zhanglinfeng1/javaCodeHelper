package completionContributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import completionContributor.impl.ConstructorCompletion;
import completionContributor.impl.MethodCompletion;
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import org.jetbrains.annotations.NotNull;
import util.StringUtil;

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
        if (null == psiElement) {
            return;
        }
        //当前光标所在的方法
        PsiMethod currentMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
        if (null == currentMethod) {
            return;
        }
        //当前光标在新的一行
        Document document = parameters.getEditor().getDocument();
        int lineNum = document.getLineNumber(parameters.getOffset());
        int lineStart = document.getLineStartOffset(lineNum);
        int lineEnd = document.getLineEndOffset(lineNum);
        String lineText = document.getText(TextRange.create(lineStart, lineEnd));
        lineText = lineText.replaceAll(COMMON_CONSTANT.WRAP_REGEX, COMMON_CONSTANT.BLANK_STRING).trim();
        if (StringUtil.isNotEmpty(lineText) && lineText.length() > psiElement.getText().trim().length()) {
            return;
        }
        BasicCompletion basicCompletion;
        if (currentMethod.isConstructor()) {
            basicCompletion = new ConstructorCompletion(currentMethod);
        } else {
            basicCompletion = new MethodCompletion(currentMethod);
        }
        List<LookupElementBuilder> elementList = basicCompletion.getLookupElement();
        if (!elementList.isEmpty()) {
            elementList.forEach(e -> result.addElement(e.withIcon(ICON_CONSTANT.BO_LUO_SVG_16).withCaseSensitivity(true)));
        }
    }

}
