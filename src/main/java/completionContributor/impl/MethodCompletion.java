package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
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
    private PsiClass returnClass;

    public MethodCompletion(PsiMethod currentMethod) {
        super(currentMethod);
        PsiType psiType = currentMethod.getReturnType();
        if (null != psiType) {
            returnClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
            returnTypeFullName = psiType.getPresentableText();
        }
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        if (StringUtil.isEmpty(returnTypeFullName)) {
            return new ArrayList<>();
        }
        if (returnTypeFullName.startsWith(TYPE_CONSTANT.LIST)) {
            String paradigmName = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
            if (match(paradigmName)) {
                String str = (TypeUtil.isObject(paradigmName) ? paradigmName : COMMON_CONSTANT.BLANK_STRING) + TYPE_CONSTANT.LIST;
                return lookupElementBuilderList(TYPE_CONSTANT.LIST_TYPE_LIST, str);
            }
        } else if (returnTypeFullName.startsWith(TYPE_CONSTANT.MAP)) {
            String[] arr = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).split(COMMON_CONSTANT.COMMA);
            if (arr.length == 2) {
                String keyType = arr[0].trim();
                String valueType = arr[1].trim();
                if (match(keyType) && match(valueType)) {
                    String str = (TypeUtil.isObject(valueType) ? valueType : COMMON_CONSTANT.BLANK_STRING) + TYPE_CONSTANT.MAP;
                    return lookupElementBuilderList(TYPE_CONSTANT.MAP_TYPE_LIST, str);
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean match(String val) {
        return !"?".equals(val) && !"T".equals(val) && StringUtil.isNotEmpty(val);
    }

    private List<LookupElementBuilder> lookupElementBuilderList(List<String> typeList, String str) {
        //TODO 优化类导入
        PsiClass psiClass = currentMethod.getContainingClass();
        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        Project returnClassProject = returnClass.getProject();
        JavaPsiFacade instance = JavaPsiFacade.getInstance(returnClassProject);
        String finalStr = returnTypeFullName + COMMON_CONSTANT.SPACE + StringUtil.toLowerCaseFirst(str) + " = new ";
        return typeList.stream().map(s -> LookupElementBuilder.create(finalStr + s + "<>();").withPresentableText("new " + s).withInsertHandler((context, item) -> {
            PsiClass importClass = instance.findClass(TYPE_CONSTANT.TYPE_MAP.get(s), GlobalSearchScope.allScope(returnClassProject));
            if (null != importClass) {
                javaFile.importClass(importClass);
            }
        })).collect(Collectors.toList());
    }
}
