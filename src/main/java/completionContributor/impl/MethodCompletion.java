package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:16
 */
public class MethodCompletion extends BasicCompletion {
    private PsiMethod currentMethod;
    private AutoCompletion autoCompletion;
    private List<LookupElementBuilder> returnList;

    @Override
    public List<LookupElementBuilder> getLookupElement(PsiMethod currentMethod) {
        this.currentMethod = currentMethod;
        autoCompletion = getAutoCompletion(currentMethod.getReturnType());
        returnList = new ArrayList<>();
        if (null != autoCompletion) {
            if (!autoCompletion.checkParameterExist(currentMethod.getBody())) {
                //根据返回类型新建对象
                addNewReturnTypeObject();
            }
            //根据返回类型，寻找构造函数，一键转化
            addTransformation();
        }
        //寻找变量的同名方法
        addSameNameMethod();
        return returnList;
    }

    private void addNewReturnTypeObject() {
        List<String> typeList = new ArrayList<>();
        if (TYPE_CONSTANT.LIST.equals(autoCompletion.getReturnTypeShortName())) {
            typeList = TYPE_CONSTANT.LIST_TYPE_LIST;
        } else if (TYPE_CONSTANT.MAP.equals(autoCompletion.getReturnTypeShortName())) {
            typeList = TYPE_CONSTANT.MAP_TYPE_LIST;
        } else if (TypeUtil.isObject(autoCompletion.getReturnTypeShortName())) {
            typeList.add(autoCompletion.getReturnTypeShortName());
        } else {
            return;
        }

        boolean needInsertHandler = typeList.size() > 1;
        returnList.addAll(typeList.stream().map(s -> {
            String code = autoCompletion.getStartCode() + COMMON_CONSTANT.NEW + COMMON_CONSTANT.SPACE + s + autoCompletion.getEndCode();
            LookupElementBuilder builder = LookupElementBuilder.create(code).withPresentableText(COMMON_CONSTANT.NEW + COMMON_CONSTANT.SPACE + s);
            if (needInsertHandler) {
                builder = builder.withInsertHandler((context, item) -> {
                    //TODO 优化类导入
                    GlobalSearchScope globalSearchScope = currentMethod.getReturnType().getResolveScope();
                    PsiClass importClass = JavaPsiFacade.getInstance(globalSearchScope.getProject()).findClass(TYPE_CONSTANT.TYPE_MAP.get(s), globalSearchScope);
                    if (null != importClass) {
                        PsiJavaFile javaFile = (PsiJavaFile) currentMethod.getContainingClass().getContainingFile();
                        javaFile.importClass(importClass);
                    }
                });
            }
            return builder;
        }).collect(Collectors.toList()));
    }

    private void addSameNameMethod() {
        PsiClass methodClass = currentMethod.getContainingClass();
        if (null == methodClass) {
            return;
        }
        String methodName = currentMethod.getName();
        Map<String, String> paramMap = Arrays.stream(currentMethod.getParameterList().getParameters()).collect(Collectors.toMap(PsiParameter::getName, p -> p.getType().getPresentableText()));
        //方法所在类的变量
        for (PsiField psiField : methodClass.getFields()) {
            PsiClass psiFieldClass = PsiUtil.resolveClassInClassTypeOnly(psiField.getType());
            if (null == psiFieldClass) {
                continue;
            }
            //变量类的方法
            loop:
            for (PsiMethod fieldMethod : psiFieldClass.getMethods()) {
                if (!methodName.equals(fieldMethod.getName())) {
                    continue;
                }
                //方法同名、返回类型一致
                AutoCompletion fieldMethodAutoCompletion = getAutoCompletion(fieldMethod.getReturnType());
                String startCode = COMMON_CONSTANT.BLANK_STRING;
                if (null != fieldMethodAutoCompletion) {
                    fieldMethodAutoCompletion.dealParameterName(currentMethod.getBody());
                    startCode = fieldMethodAutoCompletion.getStartCode();
                }
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

    private void addTransformation() {
        if (TYPE_CONSTANT.LIST.equals(autoCompletion.getReturnTypeShortName()) && StringUtil.isNotEmpty(autoCompletion.getParadigmType())) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) currentMethod.getReturnType();
            PsiType[] psiTypeArr = referenceType.getParameters();
            if (psiTypeArr.length != 1) {
                return;
            }
            //方法内的变量
            PsiCodeBlock codeBlock = currentMethod.getBody();
            if (null == codeBlock) {
                return;
            }
            Map<String, String> variableMap = new HashMap<>();
            for (PsiStatement statements : codeBlock.getStatements()) {
                if (statements instanceof PsiDeclarationStatement) {
                    PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) statements;
                    PsiElement element = declarationStatement.getFirstChild();
                    if (element instanceof PsiLocalVariable) {
                        PsiLocalVariable variable = (PsiLocalVariable) element;
                        PsiType variableType = variable.getType();
                        String variableTypeName = variableType.getCanonicalText();
                        String paradigmName = StringUtil.getFirstMatcher(variableType.getInternalCanonicalText(), COMMON_CONSTANT.PARENTHESES_REGEX).trim();
                        if (variableTypeName.startsWith(TYPE_CONSTANT.LIST_PATH) && StringUtil.isNotEmpty(paradigmName)) {
                            variableMap.put(paradigmName, variable.getName());
                        }
                    }
                }
            }
            if (variableMap.isEmpty()) {
                return;
            }
            //泛型类
            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiTypeArr[0]);
            //构造方法
            for (PsiMethod psiMethod : psiClass.getConstructors()) {
                PsiParameter[] parameterArr = psiMethod.getParameterList().getParameters();
                if (parameterArr.length != 1) {
                    continue;
                }
                PsiParameter parameter = parameterArr[0];
                String variableName = variableMap.get(parameter.getType().getCanonicalText());
                if (StringUtil.isNotEmpty(variableName)) {
                    returnList.add(LookupElementBuilder.create(autoCompletion.getStartCode() + variableName + ".stream().map(" + autoCompletion.getParadigmType() + "::new).collect(Collectors.toList());")
                            .withPresentableText(variableName + " to List<" + autoCompletion.getParadigmType() + ">"));
                    return;
                }
            }
        }
    }

    private AutoCompletion getAutoCompletion(PsiType psiType) {
        if (null == psiType) {
            return null;
        }
        String returnTypeFullName = psiType.getPresentableText();
        if (StringUtil.isEmpty(returnTypeFullName) || COMMON_CONSTANT.VOID.equals(returnTypeFullName) || returnTypeFullName.contains(COMMON_CONSTANT.LEFT_BRACKETS)) {
            return null;
        }
        AutoCompletion autoCompletion = new AutoCompletion(returnTypeFullName);
        String paradigmName = StringUtil.getFirstMatcher(returnTypeFullName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
        if (StringUtil.isNotEmpty(paradigmName)) {
            String[] paradigmNameArr = paradigmName.split(COMMON_CONSTANT.COMMA);
            if (Arrays.stream(paradigmNameArr).map(String::trim).anyMatch(TYPE_CONSTANT.GENERIC_PARADIGM_LIST::contains)) {
                return null;
            }
            autoCompletion.setReturnTypeShortName(returnTypeFullName.substring(0, returnTypeFullName.indexOf(COMMON_CONSTANT.LESS_THAN_SIGN)).trim());
            autoCompletion.setParadigmType(paradigmNameArr[paradigmNameArr.length - 1].trim());
            autoCompletion.setParameterName(StringUtil.toLowerCaseFirst(autoCompletion.getParadigmType() + autoCompletion.getReturnTypeShortName()));
        } else if (TYPE_CONSTANT.BASIC_TYPE_LIST.contains(returnTypeFullName) || TYPE_CONSTANT.COMMON_TYPE_LIST.contains(returnTypeFullName)) {
            return null;
        }
        return autoCompletion;
    }
}
