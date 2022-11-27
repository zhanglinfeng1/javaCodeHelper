package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import constant.TYPE_CONSTANT;
import util.StringUtil;

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
    private PsiType variableType;
    private String variableTypeName;

    public MethodCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod);
        PsiElement spaceElement = psiElement.getParent().getPrevSibling();
        if (spaceElement instanceof PsiWhiteSpace) {
            PsiElement eqElement = spaceElement.getPrevSibling();
            if (eqElement instanceof PsiJavaToken && COMMON_CONSTANT.EQUAL_SIGN.contains(eqElement.getText())) {
                PsiElement typeElement = psiElement.getParent().getParent().getFirstChild().getNextSibling();
                if (typeElement instanceof PsiTypeElement) {
                    variableType = ((PsiTypeElement) typeElement).getType();
                    variableTypeName = variableType.getInternalCanonicalText();
                }
            }
        }
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        if (null == variableType) {
            return returnList;
        }
        //转化
        addTransformation();
        //寻找变量的同名方法
        addMethodByVariableType();
        return returnList;
    }

    private void addMethodByVariableType() {
        PsiClass methodClass = currentMethod.getContainingClass();
        if (null == methodClass) {
            return;
        }
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
                PsiType fieldMethodReturnType = fieldMethod.getReturnType();
                if (null == fieldMethodReturnType) {
                    continue;
                }
                //返回类型一致
                if (!variableTypeName.equals(fieldMethodReturnType.getInternalCanonicalText())) {
                    continue;
                }
                //变量所在类的方法包含的参数
                PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
                StringBuilder paramStr = new StringBuilder(COMMON_CONSTANT.END_STR);
                if (psiParameterArr.length != 0) {
                    for (PsiParameter parameter : psiParameterArr) {
                        if (!parameter.getType().getPresentableText().equals(paramMap.get(parameter.getName()))) {
                            continue loop;
                        }
                    }
                    paramStr.insert(1, Arrays.stream(psiParameterArr).map(PsiParameter::getName).collect(Collectors.joining(COMMON_CONSTANT.COMMA_STR)));
                }
                String fieldStr = psiField.getName() + COMMON_CONSTANT.DOT + fieldMethod.getName();
                returnList.add(LookupElementBuilder.create(fieldStr + paramStr).withPresentableText(fieldStr));
            }
        }
    }

    private void addTransformation() {
        PsiCodeBlock currentMethodCodeBlock = currentMethod.getBody();
        String paradigmName = StringUtil.getFirstMatcher(variableTypeName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
        //List 类型且泛型不为空
        if (!variableTypeName.startsWith(TYPE_CONSTANT.LIST_PATH) || StringUtil.isEmpty(paradigmName) || null == currentMethodCodeBlock || TYPE_CONSTANT.GENERIC_PARADIGM_LIST.contains(paradigmName)) {
            return;
        }
        //当前变量类型的泛型
        PsiClassReferenceType referenceType = (PsiClassReferenceType) variableType;
        PsiType[] psiTypeArr = referenceType.getParameters();
        if (psiTypeArr.length != 1) {
            return;
        }
        //泛型类
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiTypeArr[0]);
        List<String> typeList = new ArrayList<>();
        //处理只有一个变量的构造方法
        for (PsiMethod psiMethod : psiClass.getConstructors()) {
            PsiParameter[] parameterArr = psiMethod.getParameterList().getParameters();
            if (parameterArr.length != 1) {
                continue;
            }
            typeList.add(parameterArr[0].getType().getInternalCanonicalText());
        }
        if (typeList.isEmpty()) {
            return;
        }
        //方法内的所有变量
        for (PsiStatement statements : currentMethodCodeBlock.getStatements()) {
            if (statements instanceof PsiDeclarationStatement) {
                //获取变量
                if (statements.getFirstChild() instanceof PsiLocalVariable) {
                    PsiLocalVariable currentMethodVariable = (PsiLocalVariable) statements.getFirstChild();
                    String currentMethodVariableName = currentMethodVariable.getName();
                    String currentMethodVariableTypeName = currentMethodVariable.getType().getInternalCanonicalText();
                    //List 类型且泛型不为空
                    String endCode = COMMON_CONSTANT.MAP_STR + psiClass.getName() + COMMON_CONSTANT.COLLECT_LIST_STR;
                    String presentableText = currentMethodVariableName + COMMON_CONSTANT.TO_STR + variableType.getPresentableText();
                    if (currentMethodVariableTypeName.startsWith(TYPE_CONSTANT.LIST_PATH)) {
                        String currentMethodVariableParadigmName = StringUtil.getFirstMatcher(currentMethodVariableTypeName, COMMON_CONSTANT.PARENTHESES_REGEX).trim();
                        if (typeList.contains(currentMethodVariableParadigmName)) {
                            returnList.add(LookupElementBuilder.create(currentMethodVariableName + COMMON_CONSTANT.STREAM_STR + endCode).withPresentableText(presentableText));
                        }
                    } else if (currentMethodVariableTypeName.contains(COMMON_CONSTANT.LEFT_BRACKETS)) {
                        String currentMethodVariableParadigmName = currentMethodVariableTypeName.split(COMMON_CONSTANT.LEFT_BRACKETS_REGEX)[0];
                        if (typeList.contains(currentMethodVariableParadigmName)) {
                            String startCode = COMMON_CONSTANT.ARRAYS_STREAM + currentMethodVariableName + COMMON_CONSTANT.RIGHT_PARENTHESES;
                            returnList.add(LookupElementBuilder.create(startCode + endCode).withPresentableText(presentableText)
                                    .withInsertHandler((context, item) -> {
                                        //TODO 优化类导入
                                        GlobalSearchScope globalSearchScope = variableType.getResolveScope();
                                        PsiClass importClass = JavaPsiFacade.getInstance(globalSearchScope.getProject()).findClass(TYPE_CONSTANT.ARRAYS_PATH, globalSearchScope);
                                        PsiJavaFile javaFile = (PsiJavaFile) currentMethod.getContainingClass().getContainingFile();
                                        javaFile.importClass(importClass);
                                    }));
                        }
                    }
                }
            }
        }
    }
}
