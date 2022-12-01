package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
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
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import constant.TYPE_CONSTANT;
import util.PsiObjectUtil;
import util.StringUtil;
import util.TypeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:16
 */
public class MethodCompletion extends BasicCompletion {
    /** 当前变量 */
    private PsiLocalVariable variable;
    /** 当前文本 */
    private final String currentText;
    /** 当前方法包含的变量Map */
    private final Map<String, PsiType> currentMethodVariableMap;
    /** 是否是return */
    private boolean isReturnType = false;

    public MethodCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod, psiElement);
        currentText = psiElement.getText().replace(TYPE_CONSTANT.INTELLIJ_IDEA_RULEZZZ, COMMON_CONSTANT.BLANK_STRING);
        if (psiElement instanceof PsiIdentifier && psiElement.getParent() instanceof PsiLocalVariable) {
            variable = (PsiLocalVariable) psiElement.getParent();
        } else if (psiElement.getParent().getParent() instanceof PsiReturnStatement) {
            isReturnType = true;
        }
        currentMethodVariableMap = PsiObjectUtil.getVariableMapFromMethod(currentMethod, psiElement.getTextOffset());
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        if (StringUtil.isEmpty(currentText)) {
            return returnList;
        }
        //当前元素是变量
        if (null != variable) {
            PsiType variableType = variable.getType();
            String variableName = variable.getName().replace(TYPE_CONSTANT.INTELLIJ_IDEA_RULEZZZ, COMMON_CONSTANT.BLANK_STRING);
            // 当前类包含的变量Map
            Map<String, PsiType> totalVariableMap = PsiObjectUtil.getVariableMapFromClass(currentMethodClass);
            totalVariableMap.putAll(currentMethodVariableMap);
            variableName = PsiObjectUtil.dealVariableName(variableName, variableType, totalVariableMap);
            //新建变量转化
            addTransformation(variableType, variableName + COMMON_CONSTANT.EQ_STR, variableName);
            //寻找变量的同类型方法,无参需判断参数名
            addSameType(COMMON_CONSTANT.BLANK_STRING, variableType.getInternalCanonicalText(), variableName + COMMON_CONSTANT.EQ_STR);
        } else if (isNewLine) {
            //在新的一行
            //已有变量转化
            addExistVariableTransformation();
            //寻找void类型方法
            addSameType(currentText, TYPE_CONSTANT.VOID, COMMON_CONSTANT.BLANK_STRING);
        } else if (isReturnType) {
            // 在return语句中
            PsiType currentMethodReturnType = currentMethod.getReturnType();
            if (null == currentMethodReturnType) {
                return returnList;
            }
            addTransformation(currentMethodReturnType, COMMON_CONSTANT.BLANK_STRING, currentMethodReturnType.getPresentableText());
            addSameType(currentText, currentMethodReturnType.getInternalCanonicalText(), COMMON_CONSTANT.BLANK_STRING);
        }
        return returnList;
    }

    private void addSameType(String methodName, String typeName, String code) {
        if (null == currentMethodClass) {
            return;
        }
        //当前类的方法
        findFromClass(PsiObjectUtil.getMethods(currentMethodClass, currentMethod), methodName, typeName, code);
        //方法所在类的变量
        for (PsiField psiField : currentMethodClass.getFields()) {
            PsiClass psiFieldClass = PsiUtil.resolveClassInClassTypeOnly(psiField.getType());
            if (null == psiFieldClass) {
                continue;
            }
            //变量类的方法
            findFromClass(psiFieldClass.getMethods(), methodName, typeName, code + psiField.getName() + COMMON_CONSTANT.DOT);
        }
    }

    private void findFromClass(PsiMethod[] methodArr, String methodName, String typeName, String code) {
        loop:
        for (PsiMethod fieldMethod : methodArr) {
            if (StringUtil.isNotEmpty(methodName) && !fieldMethod.getName().contains(methodName)) {
                continue;
            }
            PsiType fieldMethodReturnType = fieldMethod.getReturnType();
            //返回类型不一致
            if (null == fieldMethodReturnType || !typeName.equals(fieldMethodReturnType.getInternalCanonicalText())) {
                continue;
            }
            //变量所在类的方法包含的参数
            PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
            StringBuilder paramStr = new StringBuilder(COMMON_CONSTANT.END_STR);
            if (psiParameterArr.length == 0) {
                if (fieldMethod.getName().startsWith(COMMON_CONSTANT.GET)) {
                    continue;
                }
            } else {
                if (psiParameterArr.length == 1 && fieldMethod.getName().startsWith(COMMON_CONSTANT.SET)) {
                    continue;
                }
                for (PsiParameter parameter : psiParameterArr) {
                    if (!parameter.getType().getPresentableText().equals(currentMethodVariableMap.get(parameter.getName()).getPresentableText())) {
                        continue loop;
                    }
                }
                paramStr.insert(1, Arrays.stream(psiParameterArr).map(PsiParameter::getName).collect(Collectors.joining(COMMON_CONSTANT.COMMA_STR)));
            }
            returnList.add(LookupElementBuilder.create(code + fieldMethod.getName() + paramStr).withPresentableText(code + fieldMethod.getName()));
        }
    }

    private void addExistVariableTransformation() {
        for (Map.Entry<String, PsiType> entry : currentMethodVariableMap.entrySet()) {
            if (!entry.getKey().contains(currentText)) {
                continue;
            }
            addTransformation(entry.getValue(), entry.getKey() + COMMON_CONSTANT.EQ_STR, entry.getKey());
        }
    }

    private void addTransformation(PsiType variableType, String startCode, String presentableText) {
        //变量类型存在
        PsiClass variableTypeClass = PsiUtil.resolveClassInClassTypeOnly(variableType);
        String endCode;
        if (null == variableTypeClass || currentMethodVariableMap.isEmpty()) {
            return;
        } else if (TypeUtil.isList(variableTypeClass)) {
            endCode = COMMON_CONSTANT.COLLECT_LIST_STR;
        } else if (TypeUtil.isSet(variableTypeClass)) {
            endCode = COMMON_CONSTANT.COLLECT_SET_STR;
        } else {
            return;
        }
        //当前变量类型的泛型类
        PsiClass psiClass = PsiObjectUtil.getReferenceTypeClass(variableType);
        if (null == psiClass) {
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
            PsiType currentMethodVariableType = entry.getValue();
            PsiClass currentMethodVariableClass = PsiUtil.resolveClassInClassTypeOnly(currentMethodVariableType);
            String currentMethodVariableTypeName = currentMethodVariableType.getInternalCanonicalText();
            //list 或者 set 类型
            if (TypeUtil.isList(currentMethodVariableClass) || TypeUtil.isSet(currentMethodVariableClass)) {
                String currentMethodVariableParadigmName = StringUtil.getFirstMatcher(currentMethodVariableTypeName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
                if (typeList.contains(currentMethodVariableParadigmName)) {
                    returnList.add(LookupElementBuilder.create(startCode + currentMethodVariableName + COMMON_CONSTANT.STREAM_MAP_STR + endCode)
                            .withPresentableText(currentMethodVariableName + COMMON_CONSTANT.TO_STR + presentableText));
                }
            } else if (TypeUtil.isSimpleArr(currentMethodVariableType)) {
                //数组类型
                String currentMethodVariableParadigmName = currentMethodVariableTypeName.split(COMMON_CONSTANT.LEFT_BRACKETS_REGEX)[0];
                if (typeList.contains(currentMethodVariableParadigmName)) {
                    returnList.add(LookupElementBuilder.create(startCode + COMMON_CONSTANT.ARRAYS_STREAM_STR + currentMethodVariableName + COMMON_CONSTANT.MAP_STR + endCode)
                            .withPresentableText(currentMethodVariableName + COMMON_CONSTANT.TO_STR + presentableText)
                            .withInsertHandler((context, item) -> {
                                //TODO 优化类导入
                                GlobalSearchScope globalSearchScope = variableType.getResolveScope();
                                PsiClass importClass = JavaPsiFacade.getInstance(globalSearchScope.getProject()).findClass(TYPE_CONSTANT.ARRAYS_PATH, globalSearchScope);
                                PsiJavaFile javaFile = (PsiJavaFile) currentMethodClass.getContainingFile();
                                javaFile.importClass(importClass);
                            }));
                }
            }
        }
    }
}
