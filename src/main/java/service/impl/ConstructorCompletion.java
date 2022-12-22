package service.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import service.Completion;
import constant.COMMON;
import util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends Completion {

    public ConstructorCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod, psiElement);
    }

    @Override
    public void init() {
        if (!isNewLine) {
            return;
        }
        //构造方法的方法体
        PsiCodeBlock codeBlock = currentMethod.getBody();
        String currentMethodBodyStr = codeBlock == null ? COMMON.BLANK_STRING : codeBlock.getText();
        StringBuilder fillStr = new StringBuilder();
        //待处理的变量
        Map<String, String> fieldNameMap = Arrays.stream(currentMethodClass.getFields()).filter(f -> !currentMethodBodyStr.contains(COMMON.THIS_STR + f.getName()))
                .collect(Collectors.toMap(PsiField::getName, f -> f.getType().getInternalCanonicalText()));
        //构造方法的参数
        loop:
        for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
            if (fieldNameMap.isEmpty()){
                break;
            }
            String parameterName = parameter.getName();
            if (parameter.getType().getInternalCanonicalText().equals(fieldNameMap.get(parameterName))) {
                fillStr.append(COMMON.THIS_STR).append(parameterName).append(COMMON.EQ_STR).append(parameterName).append(COMMON.SEMICOLON);
                fieldNameMap.remove(parameterName);
                continue;
            }
            PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameter.getType());
            if (null == parameterClass) {
                continue;
            }
            //构造方法的参数为对象类型，处理对象的变量
            for (PsiField field : parameterClass.getFields()) {
                if (fieldNameMap.isEmpty()){
                    break loop;
                }
                String fieldName = field.getName();
                if (!field.getType().getInternalCanonicalText().equals(fieldNameMap.get(fieldName))) {
                    continue;
                }
                fillStr.append(COMMON.THIS_STR).append(fieldName).append(COMMON.EQ_STR).append(parameter.getName()).append(COMMON.DOT).append(COMMON.GET).append(StringUtil.toUpperCaseFirst(fieldName)).append(COMMON.END_STR);
                fieldNameMap.remove(fieldName);
            }
        }
        if (StringUtil.isNotEmpty(fillStr)) {
            returnList.add(LookupElementBuilder.create(fillStr.toString()).withPresentableText("fillConstructor")
                    .withInsertHandler((context, item) -> CodeStyleManager.getInstance(currentMethod.getProject()).reformat(currentMethod)));
        }
    }
}
