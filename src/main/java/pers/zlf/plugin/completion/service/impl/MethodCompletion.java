package pers.zlf.plugin.completion.service.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.completion.service.Completion;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.constant.CLASS_TYPE;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.TypeUtil;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:16
 */
public class MethodCompletion extends Completion {
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> currentMethodVariableMap;
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> totalVariableMap;

    public MethodCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod, psiElement);
    }

    @Override
    public void init() {
        //当前方法内的变量
        currentMethodVariableMap = MyPsiUtil.getVariableMapFromMethod(currentMethod, currentElement.getTextOffset());
        currentMethodVariableMap.remove(currentText);
        //当前类的变量
        totalVariableMap = new HashMap<>(16);
        totalVariableMap.putAll(currentMethodVariableMap);
        totalVariableMap.putAll(MyPsiUtil.getVariableMapFromClass(currentMethodClass));
        //在新的一行
        if (isNewLine) {
            //已有变量转化
            currentMethodVariableMap.entrySet().stream().filter(t -> t.getKey().contains(currentText))
                    .forEach(t -> addTransformation(t.getKey(), t.getValue(), t.getKey() + COMMON.EQ_STR));
            //寻找void类型方法
            addSameType(currentText, CLASS_TYPE.VOID, COMMON.BLANK_STRING);
        } else if (currentElement instanceof PsiIdentifier && currentElement.getParent() instanceof PsiLocalVariable) {
            //当前元素是变量
            PsiLocalVariable variable = (PsiLocalVariable) currentElement.getParent();
            String variableName = MyPsiUtil.dealVariableName(variable.getName(), variable.getType(), new ArrayList<>(currentMethodVariableMap.keySet()));
            //新建变量转化
            addTransformation(variableName, variable.getType(), variableName + COMMON.EQ_STR);
            //寻找变量的同类型方法,无参需判断参数名
            addSameType(COMMON.BLANK_STRING, variable.getType().getInternalCanonicalText(), variableName + COMMON.EQ_STR);
        } else if (currentElement.getParent().getParent() instanceof PsiReturnStatement) {
            // 在return语句中
            Optional.ofNullable(currentMethod.getReturnType()).ifPresent(psiType -> {
                addTransformation(COMMON.BLANK_STRING, psiType, COMMON.BLANK_STRING);
                addSameType(currentText, psiType.getInternalCanonicalText(), COMMON.BLANK_STRING);
            });
        }
    }

    private void addSameType(String variableName, String typeName, String code) {
        //当前类的方法
        findFromClass(currentMethodClass, MyPsiUtil.getMethods(currentMethodClass, currentMethod, variableName), typeName, code + COMMON.THIS_STR);
        //方法所在类的变量
        for (PsiField psiField : currentMethodClass.getFields()) {
            if (StringUtil.isNotEmpty(variableName) && !psiField.getName().contains(variableName)) {
                continue;
            }
            Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(psiField.getType())).filter(psiFieldClass -> !TypeUtil.isSimpleType(psiFieldClass.getName()))
                    .ifPresent(psiFieldClass -> findFromClass(psiFieldClass, psiFieldClass.getMethods(), typeName, code + psiField.getName() + COMMON.DOT));
        }
    }

    private void findFromClass(PsiClass psiClass, PsiMethod[] methodArr, String typeName, String code) {
        List<String> setAndGetMethodList = Arrays.stream(psiClass.getFields()).map(f -> COMMON.SET + StringUtil.toUpperCaseFirst(f.getName())).collect(Collectors.toList());
        setAndGetMethodList.addAll(Arrays.stream(psiClass.getFields()).map(f -> COMMON.GET + StringUtil.toUpperCaseFirst(f.getName())).collect(Collectors.toList()));
        for (PsiMethod fieldMethod : methodArr) {
            PsiType fieldMethodReturnType = fieldMethod.getReturnType();
            //返回类型不一致、set或get方法
            if (setAndGetMethodList.contains(fieldMethod.getName()) || null == fieldMethodReturnType || !typeName.equals(fieldMethodReturnType.getInternalCanonicalText())) {
                continue;
            }
            //变量所在类的方法包含的参数
            PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
            Optional.ofNullable(this.getParamNameList(psiParameterArr)).map(list -> new StringBuilder(COMMON.END_STR).insert(1, String.join(COMMON.COMMA_STR, list)))
                    .ifPresent(t -> returnList.add(LookupElementBuilder.create(code + fieldMethod.getName() + t).withPresentableText(code + fieldMethod.getName())));
        }
    }

    private List<String> getParamNameList(PsiParameter[] psiParameterArr) {
        List<String> paramNameList = new ArrayList<>();
        for (PsiParameter parameter : psiParameterArr) {
            PsiType variableType = totalVariableMap.get(parameter.getName());
            String parameterTypeStr = parameter.getType().getPresentableText();
            if (null != variableType && parameterTypeStr.equals(variableType.getPresentableText())) {
                paramNameList.add(parameter.getName());
                continue;
            }
            if (TypeUtil.isSimpleType(parameterTypeStr)) {
                return null;
            }
            totalVariableMap.entrySet().stream().filter(m -> parameterTypeStr.equals(m.getValue().getPresentableText())).map(Map.Entry::getKey).findAny().ifPresent(paramNameList::add);
        }
        return paramNameList;
    }

    private void addTransformation(String variableName, PsiType variableType, String startCode) {
        if (currentMethodVariableMap.isEmpty()) {
            return;
        }
        //当前变量类型的泛型类
        PsiClass psiClass = MyPsiUtil.getReferenceTypeClass(variableType);
        if (null == psiClass) {
            return;
        }
        //变量类型存在
        PsiClass variableTypeClass = PsiUtil.resolveClassInClassTypeOnly(variableType);
        String endCode;
        if (TypeUtil.isList(variableTypeClass)) {
            endCode = psiClass.getName() + COMMON.COLLECT_LIST_STR;
        } else if (TypeUtil.isSet(variableTypeClass)) {
            endCode = psiClass.getName() + COMMON.COLLECT_SET_STR;
        } else {
            return;
        }
        //过滤只有一个参数的构造方法
        List<String> typeList = Arrays.stream(psiClass.getConstructors()).map(m -> m.getParameterList().getParameters())
                .filter(parameterArr -> 1 == parameterArr.length)
                .map(parameterArr -> parameterArr[0].getType().getInternalCanonicalText()).collect(Collectors.toList());
        if (typeList.isEmpty()) {
            return;
        }
        //方法内的所有变量
        for (Map.Entry<String, PsiType> entry : currentMethodVariableMap.entrySet()) {
            String currentMethodVariableName = entry.getKey();
            if (currentMethodVariableName.equals(variableName)) {
                continue;
            }
            PsiType currentMethodVariableType = entry.getValue();
            String currentMethodVariableTypeName = currentMethodVariableType.getInternalCanonicalText();
            //list 或者 set 类型
            Equals.of(PsiUtil.resolveClassInClassTypeOnly(currentMethodVariableType)).and(TypeUtil::isList).or(TypeUtil::isSet)
                    .and(typeList.contains(StringUtil.getFirstMatcher(currentMethodVariableTypeName, REGEX.PARENTHESES).trim()))
                    .ifTrue(() -> returnList.add(LookupElementBuilder.create(startCode + currentMethodVariableName + COMMON.STREAM_MAP_STR + endCode)));
            //数组类型
            Equals.of(currentMethodVariableType).and(TypeUtil::isSimpleArr).and(typeList.contains(currentMethodVariableTypeName.split(REGEX.LEFT_BRACKETS)[0]))
                    .ifTrue(() -> returnList.add(LookupElementBuilder.create(startCode + String.format(COMMON.ARRAYS_STREAM_STR, currentMethodVariableName) + endCode)
                            .withInsertHandler((context, item) -> {
                                PsiJavaFile javaFile = (PsiJavaFile) currentMethodClass.getContainingFile();
                                MyPsiUtil.findClassByFullName(variableType.getResolveScope(), CLASS_TYPE.ARRAYS_PATH).ifPresent(javaFile::importClass);
                            })));
        }
    }
}
