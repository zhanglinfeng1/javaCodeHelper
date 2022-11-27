package completionContributor;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:04
 */
public abstract class BasicCompletion {
    /** 当前方法所在类 */
    public PsiClass currentMethodClass;
    /** 当前方法 */
    public PsiMethod currentMethod;
    /** 是否是新行 */
    public boolean isNewLine;
    /** 自动补全List */
    public List<LookupElementBuilder> returnList;

    public BasicCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        this.currentMethod = currentMethod;
        this.currentMethodClass = currentMethod.getContainingClass();
        this.returnList = new ArrayList<>();
        this.isNewLine = psiElement instanceof PsiIdentifier && psiElement.getParent() instanceof PsiReferenceExpression
                && psiElement.getParent().getParent() instanceof PsiExpressionStatement && psiElement.getParent().getParent().getParent() instanceof PsiCodeBlock;
    }

    public abstract List<LookupElementBuilder> getLookupElement();
}
