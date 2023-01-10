package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import constant.COMMON;
import constant.ICON;
import constant.XML;
import org.jetbrains.annotations.NotNull;
import util.MyPsiUtil;
import util.XmlUtil;

import java.util.Arrays;
import java.util.Collection;
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
    public void addLineMarker(XmlFile xmlFile, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Optional.ofNullable(XmlUtil.getRootTagByName(xmlFile, XML.MAPPER)).ifPresent(t -> MyPsiUtil.findClassByFullName(xmlFile, t.getAttributeValue(XML.NAMESPACE)).ifPresent(c -> {
            if (c.getMethods().length != 0) {
                Map<String, PsiMethod> methodMap = Arrays.stream(c.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
                XmlUtil.findTags(t, XML.INSERT, XML.UPDATE, XML.DELETE, XML.SELECT).forEach(x -> Optional.ofNullable(methodMap.get(x.getAttributeValue(XML.ID))).ifPresent(m -> result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(m).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(x))));
            }
        }));
    }
}
