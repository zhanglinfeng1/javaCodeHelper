package pers.zlf.plugin.completion.code;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/16 19:04
 */
public abstract class BaseCompletion {
    /** 当前元素 */
    protected final PsiElement currentElement;
    /** 当前文本 */
    protected final String currentText;
    /** 当前方法 */
    protected final PsiMethod currentMethod;
    /** 当前方法所在类 */
    protected final PsiClass currentMethodClass;
    /** 是否是新行 */
    protected final boolean isNewLine;
    /** 自动补全List */
    protected final List<LookupElementBuilder> returnList = new ArrayList<>();

    public BaseCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        this.currentElement = psiElement;
        this.currentText = psiElement.getText().replace(ClassType.INTELLIJ_IDEA_RULEZZZ, Common.BLANK_STRING);
        this.currentMethod = currentMethod;
        this.currentMethodClass = currentMethod.getContainingClass();
        this.isNewLine = Optional.ofNullable(PsiTreeUtil.prevVisibleLeaf(currentElement)).map(t -> Common.SEMICOLON.equals(t.getText()) || Common.RIGHT_BRACE.equals(t.getText())).orElse(false);
        Equals.of(StringUtil.isNotEmpty(currentText) && null != currentMethodClass).ifTrue(this::init);
    }

    /**
     * 处理补全内容
     */
    public abstract void init();

    public List<LookupElementBuilder> getLookupElement() {
        return returnList;
    }
}
