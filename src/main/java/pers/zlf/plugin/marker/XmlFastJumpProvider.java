package pers.zlf.plugin.marker;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/6 16:35
 */
public class XmlFastJumpProvider extends BaseLineMarkerProvider<XmlFile> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof XmlFile;
    }

    @Override
    public void dealPsiElement() {
        Optional.ofNullable(XmlUtil.getRootTagByName(element, Xml.MAPPER))
                .ifPresent(t -> MyPsiUtil.findClassByFullName(element.getResolveScope(), t.getAttributeValue(Xml.NAMESPACE))
                        .map(c -> Arrays.stream(c.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2)))
                        .filter(map -> !map.isEmpty())
                        .ifPresent(methodMap -> XmlUtil.findTags(t, Xml.INSERT, Xml.UPDATE, Xml.DELETE, Xml.SELECT)
                                .forEach(tag -> Optional.ofNullable(tag.getAttributeValue(Xml.ID)).map(methodMap::get).ifPresent(method -> addLineMarker(method, tag)))));
    }
}
