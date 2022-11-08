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
import java.util.Arrays;
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
        if (StringUtil.isEmpty(returnTypeFullName) || returnTypeFullName.contains("?") || returnTypeFullName.equals("void")) {
            return new ArrayList<>();
        }
        String returnTypeName = returnTypeFullName;
        String paradigmName = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
        String codeStr = returnTypeFullName;
        String endStr = "();";
        if (StringUtil.isNotEmpty(paradigmName)) {
            String[] paradigmNameArr = paradigmName.split(COMMON_CONSTANT.COMMA);
            if (Arrays.stream(paradigmNameArr).map(String::trim).anyMatch(s -> s.equals("T") || s.equals("E"))) {
                return new ArrayList<>();
            }
            returnTypeName = returnTypeFullName.substring(0, returnTypeFullName.indexOf(COMMON_CONSTANT.LEFT_BRACKETS)).trim();
            codeStr = paradigmNameArr[paradigmNameArr.length - 1].trim() + returnTypeName;
            endStr = "<>();";
        }
        codeStr = returnTypeFullName + COMMON_CONSTANT.SPACE + StringUtil.toLowerCaseFirst(codeStr) + " = new ";
        if (TYPE_CONSTANT.LIST.equals(returnTypeName)) {
            return lookupElementBuilderList(TYPE_CONSTANT.LIST_TYPE_LIST, codeStr, endStr);
        } else if (TYPE_CONSTANT.MAP.equals(returnTypeName)) {
            return lookupElementBuilderList(TYPE_CONSTANT.MAP_TYPE_LIST, codeStr, endStr);
        }
        if (TypeUtil.isObject(returnTypeName)) {
            //TODO 兼顾低版本，不使用List.of
            return lookupElementBuilderList(Arrays.asList(returnTypeName), codeStr, endStr);
        }
        return new ArrayList<>();
    }

    private List<LookupElementBuilder> lookupElementBuilderList(List<String> typeList, String codeStr, String endStr) {
        //TODO 优化类导入
        return typeList.stream().map(s -> {
            LookupElementBuilder builder = LookupElementBuilder.create(codeStr + s + endStr).withPresentableText("new " + s);
            if (typeList.size() > 1) {
                builder = builder.withInsertHandler((context, item) -> {
                    Project returnClassProject = returnClass.getProject();
                    PsiClass importClass = JavaPsiFacade.getInstance(returnClassProject).findClass(TYPE_CONSTANT.TYPE_MAP.get(s), GlobalSearchScope.allScope(returnClassProject));
                    if (null != importClass) {
                        PsiJavaFile javaFile = (PsiJavaFile) currentMethod.getContainingClass().getContainingFile();
                        javaFile.importClass(importClass);
                    }
                });
            }
            return builder;
        }).collect(Collectors.toList());
    }
}
