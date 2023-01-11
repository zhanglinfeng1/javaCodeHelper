package pers.zlf.plugin.marker;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import pers.zlf.plugin.constant.XML;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/6 16:35
 */
public class XmlFastJumpProvider extends AbstractLineMarkerProvider<XmlFile> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof XmlFile;
    }

    @Override
    public void dealPsiElement() {
        Optional.ofNullable(XmlUtil.getRootTagByName(element, XML.MAPPER)).ifPresent(t -> MyPsiUtil.findClassByFullName(element, t.getAttributeValue(XML.NAMESPACE))
                .map(c -> Arrays.stream(c.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2))).map(map -> map.isEmpty() ? null : map)
                .ifPresent(methodMap -> XmlUtil.findTags(t, XML.INSERT, XML.UPDATE, XML.DELETE, XML.SELECT)
                        .forEach(tag -> Optional.ofNullable(methodMap.get(tag.getAttributeValue(XML.ID))).ifPresent(method -> addLineMarker(method, tag)))));
    }
}
