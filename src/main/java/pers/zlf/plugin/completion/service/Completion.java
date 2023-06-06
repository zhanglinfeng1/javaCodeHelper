package pers.zlf.plugin.completion.service;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.CLASS_TYPE;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:04
 */
public abstract class Completion {
    /** 当前元素 */
    public PsiElement currentElement;
    /** 当前文本 */
    public String currentText;
    /** 当前方法 */
    public PsiMethod currentMethod;
    /** 当前方法所在类 */
    public PsiClass currentMethodClass;
    /** 是否是新行 */
    public boolean isNewLine;
    /** 自动补全List */
    public List<LookupElementBuilder> returnList = new ArrayList<>();

    public Completion(PsiMethod currentMethod, PsiElement psiElement) {
        this.currentElement = psiElement;
        this.currentText = psiElement.getText().replace(CLASS_TYPE.INTELLIJ_IDEA_RULEZZZ, COMMON.BLANK_STRING);
        this.currentMethod = currentMethod;
        this.currentMethodClass = currentMethod.getContainingClass();
        this.isNewLine = Optional.ofNullable(PsiTreeUtil.prevVisibleLeaf(currentElement)).map(t -> COMMON.SEMICOLON.equals(t.getText()) || COMMON.LEFT_BRACE.equals(t.getText())).orElse(false);
        Equals.of(StringUtil.isNotEmpty(currentText) && null != currentMethodClass).ifTrue(this::init);
    }

    public abstract void init();

    public List<LookupElementBuilder> getLookupElement() {
        return returnList;
    }
}
