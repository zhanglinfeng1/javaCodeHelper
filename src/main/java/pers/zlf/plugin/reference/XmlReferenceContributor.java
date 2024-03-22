package pers.zlf.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.XmlUtil;

import java.util.Optional;

/**
 * xml中的跳转
 *
 * @author zhanglinfeng
 * @date create in 2024/3/6 17:32
 */
public class XmlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        //resultMap、refid标签跳转
        psiReferenceRegistrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue(Xml.RESULT_MAP, Xml.REFID), new PsiReferenceProvider() {
            @Override
            public @NotNull PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                XmlAttributeValue resultMapAttributeValue = (XmlAttributeValue) element;
                XmlTag mapperTag = XmlUtil.getRootTagByName((XmlFile) resultMapAttributeValue.getContainingFile(), Xml.MAPPER);
                String resultMapValue = resultMapAttributeValue.getValue();
                Optional<XmlTag> tagOptional = XmlUtil.findTags(mapperTag, Xml.RESULT_MAP, Xml.SQL).stream().filter(t -> resultMapValue.equals(t.getAttributeValue(Xml.ID))).findAny();
                if (tagOptional.isPresent()) {
                    return new PsiReferenceBase[]{new XmlReference<>(resultMapAttributeValue, new TextRange(1, resultMapValue.length() + 1), tagOptional.get())};
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });

    }
}
