package pers.zlf.plugin.marker;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/6 16:35
 */
public class XmlLineMarkerProvider extends BaseLineMarkerProvider<XmlFile> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof XmlFile;
    }

    @Override
    public void dealPsiElement() {
        // 判断是否是mapper.xml
        XmlTag mapperTag = XmlUtil.getRootTagByName(currentElement, Xml.MAPPER);
        if (null == mapperTag){
            return;
        }
        // 获取mapper.xml所绑定类的方法
        Optional<Map<String, PsiMethod>> classMethodOptional = MyPsiUtil.findClassByFullName(currentElement.getResolveScope(), mapperTag.getAttributeValue(Xml.NAMESPACE))
                .map(c -> Arrays.stream(c.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2)))
                .filter(map -> !map.isEmpty());
        if (classMethodOptional.isPresent()) {
            Map<String, PsiMethod> methodMap = classMethodOptional.get();
            //查询insert、update、delete、select标签
            List<XmlTag> tagList = XmlUtil.findTags(mapperTag, Xml.INSERT, Xml.UPDATE, Xml.DELETE, Xml.SELECT);
            //获取标签的id属性，与方法名匹配
            tagList.forEach(tag -> Optional.ofNullable(tag.getAttributeValue(Xml.ID)).map(methodMap::get).ifPresent(method -> addLineMarker(tag, method.getNameIdentifier())));
        }
    }
}
