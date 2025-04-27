package pers.zlf.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/7 17:02
 */
public class XmlReference<T extends PsiElement, R extends PsiElement> extends PsiReferenceBase<R> {
    private final T TARGET_ELEMENT;

    public XmlReference(@NotNull R element, TextRange rangeInElement, T targetElement) {
        super(element, rangeInElement);
        this.TARGET_ELEMENT = targetElement;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return TARGET_ELEMENT;
    }
}
