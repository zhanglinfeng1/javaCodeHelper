package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import constant.TYPE_CONSTANT;
import pojo.AutoCompletion;
import util.StringUtil;
import util.TypeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:16
 */
public class MethodCompletion extends BasicCompletion {

    @Override
    public List<LookupElementBuilder> getLookupElement(PsiMethod currentMethod) {
        List<LookupElementBuilder> returnList = new ArrayList<>();
        //根据返回类型新建对象
        addNewReturnTypeObject(returnList, currentMethod);
        //寻找变量的同名方法
        addSameMethodName(returnList, currentMethod);
        return returnList;
    }

    private void addNewReturnTypeObject(List<LookupElementBuilder> returnList, PsiMethod currentMethod) {
        PsiType psiType = currentMethod.getReturnType();
        AutoCompletion autoCompletion = getAutoCompletion(psiType);
        if (null == autoCompletion) {
            return;
        }
        List<String> typeList = new ArrayList<>();
        if (TYPE_CONSTANT.LIST.equals(autoCompletion.getReturnTypeName())) {
            typeList = TYPE_CONSTANT.LIST_TYPE_LIST;
        } else if (TYPE_CONSTANT.MAP.equals(autoCompletion.getReturnTypeName())) {
            typeList = TYPE_CONSTANT.MAP_TYPE_LIST;
        } else if (TypeUtil.isObject(autoCompletion.getReturnTypeName())) {
            typeList.add(autoCompletion.getReturnTypeName());
        }
        PsiClass returnClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        List<String> finalTypeList = typeList;
        returnList.addAll(typeList.stream().map(s -> {
            String code = autoCompletion.getStartCode() + COMMON_CONSTANT.NEW + COMMON_CONSTANT.SPACE + s + autoCompletion.getEndCode();
            LookupElementBuilder builder = LookupElementBuilder.create(code).withPresentableText(COMMON_CONSTANT.NEW + COMMON_CONSTANT.SPACE + s);
            if (finalTypeList.size() > 1) {
                builder = builder.withInsertHandler((context, item) -> {
                    //TODO 优化类导入
                    Project returnClassProject = returnClass.getProject();
                    PsiClass importClass = JavaPsiFacade.getInstance(returnClassProject).findClass(TYPE_CONSTANT.TYPE_MAP.get(s), GlobalSearchScope.allScope(returnClassProject));
                    if (null != importClass) {
                        PsiJavaFile javaFile = (PsiJavaFile) currentMethod.getContainingClass().getContainingFile();
                        javaFile.importClass(importClass);
                    }
                });
            }
            return builder;
        }).collect(Collectors.toList()));
    }

    private void addSameMethodName(List<LookupElementBuilder> returnList, PsiMethod currentMethod) {
        PsiClass methodClass = currentMethod.getContainingClass();
        if (null == methodClass) {
            return;
        }
        String methodName = currentMethod.getName();
        Map<String, String> paramMap = Arrays.stream(currentMethod.getParameterList().getParameters()).collect(Collectors.toMap(PsiParameter::getName, p -> p.getType().getPresentableText()));
        for (PsiField psiField : methodClass.getFields()) {
            PsiClass psiFieldClass = PsiUtil.resolveClassInClassTypeOnly(psiField.getType());
            if (null == psiFieldClass) {
                continue;
            }
            loop:
            for (PsiMethod fieldMethod : psiFieldClass.getMethods()) {
                if (!methodName.equals(fieldMethod.getName())) {
                    continue;
                }
                AutoCompletion autoCompletion = getAutoCompletion(fieldMethod.getReturnType());
                String startCode = null == autoCompletion ? COMMON_CONSTANT.BLANK_STRING : autoCompletion.getStartCode();
                PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
                if (psiParameterArr.length == 0) {
                    returnList.add(LookupElementBuilder.create(startCode + psiField.getName() + COMMON_CONSTANT.DOT + fieldMethod.getName() + COMMON_CONSTANT.END_STR).withPresentableText(psiField.getName() + COMMON_CONSTANT.DOT + fieldMethod.getName()));
                } else {
                    for (PsiParameter parameter : psiParameterArr) {
                        if (!parameter.getType().getPresentableText().equals(paramMap.get(parameter.getName()))) {
                            continue loop;
                        }
                    }
                    StringBuilder paramStr = new StringBuilder(COMMON_CONSTANT.END_STR);
                    paramStr.insert(1, Arrays.stream(psiParameterArr).map(PsiParameter::getName).collect(Collectors.joining(COMMON_CONSTANT.COMMA_STR)));
                    returnList.add(LookupElementBuilder.create(startCode + psiField.getName() + COMMON_CONSTANT.DOT + fieldMethod.getName() + paramStr)
                            .withPresentableText(psiField.getName() + COMMON_CONSTANT.DOT + fieldMethod.getName()));
                }
            }
        }
    }

    private AutoCompletion getAutoCompletion(PsiType psiType) {
        if (null == psiType) {
            return null;
        }
        String returnTypeFullName = psiType.getPresentableText();
        if (StringUtil.isEmpty(returnTypeFullName) || COMMON_CONSTANT.VOID.equals(returnTypeFullName)) {
            return null;
        }
        AutoCompletion autoCompletion = new AutoCompletion();
        autoCompletion.setReturnTypeName(returnTypeFullName);
        autoCompletion.setStartCode(returnTypeFullName);
        autoCompletion.setEndCode(COMMON_CONSTANT.END_STR);
        String paradigmName = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
        if (StringUtil.isNotEmpty(paradigmName)) {
            String[] paradigmNameArr = paradigmName.split(COMMON_CONSTANT.COMMA);
            if (Arrays.stream(paradigmNameArr).map(String::trim).anyMatch(TYPE_CONSTANT.GENERIC_PARADIGM_LIST::contains)) {
                return null;
            }
            autoCompletion.setReturnTypeName(returnTypeFullName.substring(0, returnTypeFullName.indexOf(COMMON_CONSTANT.LESS_THAN_SIGN)).trim());
            autoCompletion.setStartCode(paradigmNameArr[paradigmNameArr.length - 1].trim() + autoCompletion.getReturnTypeName());
            autoCompletion.setEndCode(COMMON_CONSTANT.GENERIC_PARADIGM_END_STR);
        } else if (returnTypeFullName.contains(COMMON_CONSTANT.LEFT_BRACKETS)) {
            autoCompletion.setReturnTypeName(returnTypeFullName.substring(0, returnTypeFullName.indexOf(COMMON_CONSTANT.LEFT_BRACKETS)).trim());
            autoCompletion.setStartCode(autoCompletion.getReturnTypeName() + COMMON_CONSTANT.ARR_STR);
        } else if (TYPE_CONSTANT.BASIC_TYPE_LIST.contains(returnTypeFullName) || TYPE_CONSTANT.COMMON_TYPE_LIST.contains(returnTypeFullName)) {
            autoCompletion.setStartCode(COMMON_CONSTANT.OBJ_STR);
        }
        autoCompletion.setStartCode(returnTypeFullName + COMMON_CONSTANT.SPACE + StringUtil.toLowerCaseFirst(autoCompletion.getStartCode()) + COMMON_CONSTANT.EQ_STR);
        return autoCompletion;
    }
}
