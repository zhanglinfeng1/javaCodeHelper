package pers.zlf.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/7 17:02
 */
public class XmlTagPsiReference extends PsiReferenceBase<XmlAttributeValue> {
    private final XmlTag targetXmlTag;

    public XmlTagPsiReference(@NotNull XmlAttributeValue element, TextRange rangeInElement, XmlTag targetXmlTag) {
        super(element, rangeInElement);
        this.targetXmlTag = targetXmlTag;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return targetXmlTag;
    }
}
