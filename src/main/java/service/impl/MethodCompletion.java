package service.impl;

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
import constant.COMMON;
import constant.REGEX;
import constant.TYPE;
import service.Completion;
import util.MyPsiUtil;
import util.StringUtil;
import util.TypeUtil;

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
        totalVariableMap = new HashMap<>();
        totalVariableMap.putAll(currentMethodVariableMap);
        //当前类的变量
        totalVariableMap.putAll(MyPsiUtil.getVariableMapFromClass(currentMethodClass));
        //在新的一行
        if (isNewLine) {
            //已有变量转化
            currentMethodVariableMap.entrySet().stream().filter(t->t.getKey().contains(currentText))
                    .forEach(t->addTransformation(t.getKey(), t.getValue(), t.getKey() + COMMON.EQ_STR));
            //寻找void类型方法
            addSameType(currentText, TYPE.VOID, COMMON.BLANK_STRING);
        } else if (currentElement instanceof PsiIdentifier && currentElement.getParent() instanceof PsiLocalVariable) {
            //当前元素是变量
            PsiLocalVariable variable = (PsiLocalVariable) currentElement.getParent();
            PsiType variableType = variable.getType();
            String variableName = MyPsiUtil.dealVariableName(variable.getName(), variableType, totalVariableMap);
            //新建变量转化
            addTransformation(variableName, variableType, variableName + COMMON.EQ_STR);
            //寻找变量的同类型方法,无参需判断参数名
            addSameType(COMMON.BLANK_STRING, variableType.getInternalCanonicalText(), variableName + COMMON.EQ_STR);
        } else if (currentElement.getParent().getParent() instanceof PsiReturnStatement) {
            // 在return语句中
            Optional.ofNullable(currentMethod.getReturnType()).ifPresent(t -> {
                addTransformation(COMMON.BLANK_STRING, t, COMMON.BLANK_STRING);
                addSameType(currentText, t.getInternalCanonicalText(), COMMON.BLANK_STRING);
            });
        }
    }

    private void addSameType(String variableName, String typeName, String code) {
        //当前类的方法
        findFromClass(MyPsiUtil.getMethods(currentMethodClass, currentMethod), typeName, code);
        //方法所在类的变量
        for (PsiField psiField : currentMethodClass.getFields()) {
            if (StringUtil.isNotEmpty(variableName) && !psiField.getName().contains(variableName)) {
                continue;
            }
            PsiClass psiFieldClass = PsiUtil.resolveClassInClassTypeOnly(psiField.getType());
            if (null == psiFieldClass || TYPE.BASIC_TYPE_LIST.contains(psiFieldClass.getName()) || TYPE.COMMON_TYPE_LIST.contains(psiFieldClass.getName()) || TYPE.COMMON_COLLECT_LIST.contains(psiFieldClass.getName())) {
                continue;
            }
            //变量类的方法
            findFromClass(psiFieldClass.getMethods(), typeName, code + psiField.getName() + COMMON.DOT);
        }
    }

    private void findFromClass(PsiMethod[] methodArr, String typeName, String code) {
        loop:
        for (PsiMethod fieldMethod : methodArr) {
            PsiType fieldMethodReturnType = fieldMethod.getReturnType();
            //返回类型不一致
            if (null == fieldMethodReturnType || !typeName.equals(fieldMethodReturnType.getInternalCanonicalText())) {
                continue;
            }
            //变量所在类的方法包含的参数
            PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
            StringBuilder paramStr = new StringBuilder(COMMON.END_STR);
            if (psiParameterArr.length == 0) {
                if (fieldMethod.getName().startsWith(COMMON.GET)) {
                    continue;
                }
            } else {
                if (psiParameterArr.length == 1 && fieldMethod.getName().startsWith(COMMON.SET)) {
                    continue;
                }
                for (PsiParameter parameter : psiParameterArr) {
                    PsiType variableType = totalVariableMap.get(parameter.getName());
                    if (null == variableType || !parameter.getType().getPresentableText().equals(variableType.getPresentableText())) {
                        continue loop;
                    }
                }
                paramStr.insert(1, Arrays.stream(psiParameterArr).map(PsiParameter::getName).collect(Collectors.joining(COMMON.COMMA_STR)));
            }
            returnList.add(LookupElementBuilder.create(code + fieldMethod.getName() + paramStr).withPresentableText(code + fieldMethod.getName()));
        }
    }

    private void addTransformation(String variableName, PsiType variableType, String startCode) {
        if (currentMethodVariableMap.isEmpty()) {
            return;
        }
        //变量类型存在
        PsiClass variableTypeClass = PsiUtil.resolveClassInClassTypeOnly(variableType);
        String endCode;
        if (TypeUtil.isList(variableTypeClass)) {
            endCode = COMMON.COLLECT_LIST_STR;
        } else if (TypeUtil.isSet(variableTypeClass)) {
            endCode = COMMON.COLLECT_SET_STR;
        } else {
            return;
        }
        //当前变量类型的泛型类
        PsiClass psiClass = MyPsiUtil.getReferenceTypeClass(variableType);
        if (null == psiClass || TYPE.BASIC_TYPE_LIST.contains(psiClass.getName()) || TYPE.COMMON_TYPE_LIST.contains(psiClass.getName()) || TYPE.COMMON_COLLECT_LIST.contains(psiClass.getName())) {
            return;
        }
        endCode = psiClass.getName() + endCode;
        //过滤只有一个参数的构造方法
        List<String> typeList = Arrays.stream(psiClass.getConstructors()).filter(m -> 1 == m.getParameterList().getParameters().length)
                .map(m -> m.getParameterList().getParameters()[0].getType().getInternalCanonicalText()).collect(Collectors.toList());
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
            PsiClass currentMethodVariableClass = PsiUtil.resolveClassInClassTypeOnly(currentMethodVariableType);
            String currentMethodVariableTypeName = currentMethodVariableType.getInternalCanonicalText();
            //list 或者 set 类型
            if (TypeUtil.isList(currentMethodVariableClass) || TypeUtil.isSet(currentMethodVariableClass)) {
                String currentMethodVariableParadigmName = StringUtil.getFirstMatcher(currentMethodVariableTypeName, REGEX.PARENTHESES).trim();
                if (typeList.contains(currentMethodVariableParadigmName)) {
                    returnList.add(LookupElementBuilder.create(startCode + currentMethodVariableName + COMMON.STREAM_MAP_STR + endCode));
                }
            } else if (TypeUtil.isSimpleArr(currentMethodVariableType)) {
                //数组类型
                String currentMethodVariableParadigmName = currentMethodVariableTypeName.split(REGEX.LEFT_BRACKETS)[0];
                if (typeList.contains(currentMethodVariableParadigmName)) {
                    returnList.add(LookupElementBuilder.create(startCode + COMMON.ARRAYS_STREAM_STR + currentMethodVariableName + COMMON.MAP_STR + endCode)
                            .withInsertHandler((context, item) -> {
                                //TODO 优化类导入
                                MyPsiUtil.findClassByFullName(variableType.getResolveScope(), TYPE.ARRAYS_PATH).ifPresent(c -> {
                                    PsiJavaFile javaFile = (PsiJavaFile) currentMethodClass.getContainingFile();
                                    javaFile.importClass(c);
                                });
                            }));
                }
            }
        }
    }
}
