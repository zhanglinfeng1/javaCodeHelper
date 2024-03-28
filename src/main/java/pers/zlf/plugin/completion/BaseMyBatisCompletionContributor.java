package pers.zlf.plugin.completion;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlTag;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.TypeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 15:05
 */
public abstract class BaseMyBatisCompletionContributor extends BaseCompletionContributor {
    /** mapper 标签 */
    protected XmlTag mapperTag;
    /** 当前标签 */
    protected XmlTag currentTag;
    /** 补全内容 */
    protected List<String> completionTextList;
    /** 标签对应的方法 */
    protected Map<String, PsiParameter> parameterMap;

    protected void completionVariable() {
        PsiClass psiClass = MyPsiUtil.findClassByFullName(currentElement.getResolveScope(), mapperTag.getAttributeValue(Xml.NAMESPACE)).orElse(null);
        if (null == psiClass) {
            return;
        }
        String methodName = findMethodName(currentTag);
        PsiMethod currentMethod = Arrays.stream(psiClass.getMethods()).filter(t -> t.getName().equals(methodName)).findAny().orElse(null);
        if (null == currentMethod) {
            return;
        }
        int parameterNum = currentMethod.getParameterList().getParameters().length;
        for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
            PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(parameter.getAnnotations(), List.of(Annotation.IBATIS_PARAM));
            if (null == psiAnnotation && parameterNum !=1){
                continue;
            }
            String prefixStr;
            if (null == psiAnnotation){
                prefixStr = Common.BLANK_STRING ;
            }else {
                String parameterName = MyPsiUtil.getAnnotationValue(psiAnnotation, Annotation.VALUE);
                parameterMap.put(parameterName, parameter);
                completionTextList.add(parameterName);
                PsiType psiType = parameter.getType();
                if (TypeUtil.isSimpleType(psiType.getPresentableText())) {
                    continue;
                }
                prefixStr = parameterName + Common.DOT;
            }
            Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(parameter.getType())).map(MyPsiUtil::getTotalFieldList).ifPresent(fieldList ->
                    fieldList.forEach(field -> completionTextList.add(prefixStr + field.getName())));
        }
    }

    protected void completionAttributeValue() {
        PsiElement attribute = currentElement.getParent().getParent();
        String tagName = currentTag.getName();
        String attributeName = attribute.getFirstChild().getText();
        BiPredicate<String, String> tagMatch = (t, u) -> tagName.equals(t) && attributeName.equals(u);
        if (tagMatch.test(Xml.FOREACH, Xml.COLLECTION) || tagMatch.test(Xml.IF, Xml.TEST) || tagMatch.test(Xml.WHEN, Xml.TEST)) {
            completionVariable();
        } else if (tagMatch.test(Xml.RESULT, Xml.PROPERTY)) {
            XmlTag resultMapTag = currentTag.getParentTag();
            if (null == resultMapTag) {
                return;
            }
            MyPsiUtil.findClassByFullName(currentElement.getResolveScope(), resultMapTag.getAttributeValue(Xml.TYPE))
                    .ifPresent(psiClass -> MyPsiUtil.getTotalFieldList(psiClass).forEach(field -> completionTextList.add(field.getName())));
        }
    }

    protected void dealForeachTag(XmlTag tag) {
        if (tag == null || CollectionUtil.isEmpty(completionTextList) || parameterMap.isEmpty()) {
            return;
        }
        if (tag.getName().equals(Xml.FOREACH)) {
            String collection = tag.getAttributeValue(Xml.COLLECTION);
            String item = tag.getAttributeValue(Xml.ITEM);
            if (StringUtil.isEmpty(collection) || StringUtil.isEmpty(item)) {
                return;
            }
            completionTextList.remove(collection);
            completionTextList.add(item);
            PsiClass psiClass = Optional.ofNullable(parameterMap.get(collection)).map(PsiParameter::getType).map(MyPsiUtil::getReferenceTypeClass).orElse(null);
            if (null == psiClass || TypeUtil.isSimpleType(psiClass.getName())) {
                return;
            }
            MyPsiUtil.getPsiFieldList(psiClass).forEach(field -> completionTextList.add(item + Common.DOT + field.getName()));
            return;
        }
        dealForeachTag(tag.getParentTag());
    }

    protected String findMethodName(XmlTag tag) {
        if (null == tag) {
            return Common.BLANK_STRING;
        }
        String tagName = tag.getName();
        switch (tagName) {
            case Xml.SELECT:
            case Xml.INSERT:
            case Xml.UPDATE:
            case Xml.DELETE:
                return tag.getAttributeValue(Xml.ID);
            default:
                break;
        }
        return findMethodName(tag.getParentTag());
    }

}
