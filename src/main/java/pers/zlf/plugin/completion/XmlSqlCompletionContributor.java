package pers.zlf.plugin.completion;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.TypeUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 15:05
 */
public class XmlSqlCompletionContributor extends BaseCompletionContributor {
    /** mapper 标签 */
    private XmlTag mapperTag;
    /** 当前标签 */
    private XmlTag currentTag;
    /** 补全内容 */
    private List<String> completionTextList;
    /** 标签对应的方法 */
    private Map<String, PsiParameter> parameterMap;

    @Override
    protected boolean check() {
        if (!(currentElement instanceof XmlToken)) {
            return false;
        }
        PsiFile file = parameters.getOriginalFile();
        if (file instanceof XmlFile) {
            currentTag = PsiTreeUtil.getParentOfType(currentElement, XmlTag.class);
            mapperTag = XmlUtil.getRootTagByName((XmlFile) file, Xml.MAPPER);
            return null != currentTag && null != mapperTag;
        }
        return false;
    }

    @Override
    protected void completion() {
        completionTextList = new ArrayList<>();
        parameterMap = new HashMap<>();
        IElementType currentElementType = ((XmlToken) currentElement).getTokenType();
        boolean completionVariable = currentText.lastIndexOf(Common.HASH + Common.LEFT_BRACE) > currentText.lastIndexOf(Common.RIGHT_BRACE);
        if (completionVariable && currentElementType == XmlTokenType.XML_DATA_CHARACTERS) {
            //补全变量
            completionVariable();
        } else if (currentElementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            //补全标签
            completionAttributeValue();
        }
        //处理foreach标签中的item
        dealForeachTag(currentTag);
        completionTextList.forEach(this::addCompletionResult);
    }

    private void completionVariable() {
        PsiClass psiClass = MyPsiUtil.findClassByFullName(currentElement.getResolveScope(), mapperTag.getAttributeValue(Xml.NAMESPACE)).orElse(null);
        if (null == psiClass) {
            return;
        }
        String methodName = findMethodName(currentTag);
        PsiMethod currentMethod = Arrays.stream(psiClass.getMethods()).filter(t -> t.getName().equals(methodName)).findAny().orElse(null);
        if (null == currentMethod) {
            return;
        }
        for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
            PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(parameter.getAnnotations(), List.of(Annotation.IBATIS_PARAM));
            String parameterName = null == psiAnnotation ? parameter.getName() : MyPsiUtil.getAnnotationValue(psiAnnotation, Annotation.VALUE);
            parameterMap.put(parameterName, parameter);
            completionTextList.add(parameterName);
            PsiType psiType = parameter.getType();
            if (TypeUtil.isSimpleType(psiType.getPresentableText())) {
                continue;
            }
            PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameter.getType());
            if (null != parameterClass && parameterClass.getImplementsListTypes().length == 0) {
                MyPsiUtil.getPsiFieldList(parameterClass).forEach(field -> completionTextList.add(parameterName + Common.DOT + field.getName()));
            }
        }
    }

    private void completionAttributeValue() {
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
                    .ifPresent(psiClass -> MyPsiUtil.getPsiFieldList(psiClass).forEach(field -> completionTextList.add(field.getName())));
        }
    }

    private void dealForeachTag(XmlTag tag) {
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

    private String findMethodName(XmlTag tag) {
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
