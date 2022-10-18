package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import constant.TYPE_CONSTANT;
import util.StringUtil;
import util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:16
 */
public class MethodCompletion extends BasicCompletion {
    private String returnTypeFullName;

    public MethodCompletion(PsiMethod currentMethod) {
        super(currentMethod);
        PsiType psiType = currentMethod.getReturnType();
        if (null != psiType) {
            returnTypeFullName = psiType.getPresentableText();
        }
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        List<LookupElementBuilder> list = new ArrayList<>();
        if (StringUtil.isEmpty(returnTypeFullName)) {
            return list;
        }
        String eqStr = " = new ";
        String newStr = "new ";
        String endStr = "<>();";
        if (returnTypeFullName.startsWith(TYPE_CONSTANT.LIST)) {
            String paradigmName = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
            if (match(paradigmName)) {
                String str = (TypeUtil.isObject(paradigmName) ? paradigmName : COMMON_CONSTANT.BLANK_STRING) + TYPE_CONSTANT.LIST;
                String finalStr = returnTypeFullName + COMMON_CONSTANT.SPACE + StringUtil.toLowerCaseFirst(str) + eqStr;
                list.addAll(TYPE_CONSTANT.LIST_TYPE_LIST.stream().map(s -> LookupElementBuilder.create(finalStr + s + endStr).withPresentableText(newStr + s)).collect(Collectors.toList()));
            }
        } else if (returnTypeFullName.startsWith(TYPE_CONSTANT.MAP)) {
            String[] arr = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).split(COMMON_CONSTANT.COMMA);
            if (arr.length == 2) {
                String keyType = arr[0].trim();
                String valueType = arr[1].trim();
                if (match(keyType) && match(valueType)) {
                    String str = (TypeUtil.isObject(valueType) ? valueType : COMMON_CONSTANT.BLANK_STRING) + TYPE_CONSTANT.MAP;
                    String finalStr = returnTypeFullName + COMMON_CONSTANT.SPACE + StringUtil.toLowerCaseFirst(str) + eqStr;
                    list.addAll(TYPE_CONSTANT.MAP_TYPE_LIST.stream().map(s -> LookupElementBuilder.create(finalStr + s + endStr).withPresentableText(newStr + s)).collect(Collectors.toList()));
                }
            }
        }
        return list;
    }

    private boolean match(String val) {
        return !"?".equals(val) && !"T".equals(val) && StringUtil.isNotEmpty(val);
    }

}
