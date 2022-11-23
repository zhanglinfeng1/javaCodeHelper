package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends BasicCompletion {

    @Override
    public List<LookupElementBuilder> getLookupElement(PsiMethod currentMethod) {
        List<LookupElementBuilder> lookupElementBuilderList = new ArrayList<>();
        PsiClass psiClass = currentMethod.getContainingClass();
        if (null == psiClass) {
            return lookupElementBuilderList;
        }
        //构造方法的方法体
        String currentMethodBodyStr = COMMON_CONSTANT.BLANK_STRING;
        PsiCodeBlock codeBlock = currentMethod.getBody();
        if (codeBlock != null) {
            currentMethodBodyStr = codeBlock.getText();
        }
        List<String> addParameterList = new ArrayList<>();
        List<String> existFieldNameList = new ArrayList<>();
        //构造方法所在类的变量
        for (PsiField methodClassField : psiClass.getFields()) {
            String fieldName = methodClassField.getName();
            if (currentMethodBodyStr.contains(COMMON_CONSTANT.THIS_STR + fieldName)) {
                existFieldNameList.add(fieldName);
                continue;
            }
            //构造方法的参数
            for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
                if (existFieldNameList.contains(parameter.getName())) {
                    continue;
                }
                if (parameter.getName().equals(fieldName)) {
                    addParameterList.add(COMMON_CONSTANT.THIS_STR + fieldName + COMMON_CONSTANT.EQ_STR + fieldName + COMMON_CONSTANT.SEMICOLON);
                    existFieldNameList.add(fieldName);
                    continue;
                }
                PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameter.getType());
                if (null == parameterClass) {
                    continue;
                }
                //构造方法的参数为对象类型，处理对象的变量
                for (PsiField field : parameterClass.getFields()) {
                    if (existFieldNameList.contains(field.getName())) {
                        continue;
                    }
                    if (field.getName().equals(fieldName)) {
                        addParameterList.add(COMMON_CONSTANT.THIS_STR + fieldName + COMMON_CONSTANT.EQ_STR + parameter.getName() + COMMON_CONSTANT.GET_STR + StringUtil.toUpperCaseFirst(fieldName) + COMMON_CONSTANT.END_STR);
                        existFieldNameList.add(fieldName);
                    }
                }
            }
        }

        String str = addParameterList.stream().filter(StringUtil::isNotEmpty).collect(Collectors.joining(COMMON_CONSTANT.BLANK_STRING));
        if (StringUtil.isNotEmpty(str)) {
            lookupElementBuilderList.add(LookupElementBuilder.create(str).withPresentableText("fillConstructor").withInsertHandler((context, item) -> {
                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(currentMethod.getProject());
                codeStyleManager.reformat(currentMethod);
            }));
        }
        return lookupElementBuilderList;
    }

}
