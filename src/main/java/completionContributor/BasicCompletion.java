package completionContributor;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:04
 */
public class BasicCompletion {
    public PsiMethod currentMethod;

    public BasicCompletion(PsiMethod currentMethod) {
        this.currentMethod = currentMethod;
    }

    public List<LookupElementBuilder> getLookupElement() {
        return new ArrayList<>();
    }
}
