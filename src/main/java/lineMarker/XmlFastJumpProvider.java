package lineMarker;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import constant.XML;
import util.MyPsiUtil;
import util.XmlUtil;

import java.util.Arrays;
import java.util.Map;
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
        Optional.ofNullable(XmlUtil.getRootTagByName(element, XML.MAPPER)).ifPresent(t -> MyPsiUtil.findClassByFullName(element, t.getAttributeValue(XML.NAMESPACE)).ifPresent(c -> {
            Map<String, PsiMethod> methodMap = Arrays.stream(c.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
            if (!methodMap.isEmpty()) {
                XmlUtil.findTags(t, XML.INSERT, XML.UPDATE, XML.DELETE, XML.SELECT).forEach(x -> Optional.ofNullable(methodMap.get(x.getAttributeValue(XML.ID))).ifPresent(m -> addLineMarker(m, x)));
            }
        }));
    }
}
