package completionContributor.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import completionContributor.BasicCompletion;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends BasicCompletion {

    public ConstructorCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod, psiElement);
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        if (null == currentMethod || !isNewLine) {
            return returnList;
        }
        PsiClass psiClass = currentMethod.getContainingClass();
        if (null == psiClass) {
            return returnList;
        }
        //构造方法的方法体
        PsiCodeBlock codeBlock = currentMethod.getBody();
        String currentMethodBodyStr = codeBlock == null ? COMMON_CONSTANT.BLANK_STRING : codeBlock.getText();
        List<String> addParameterList = new ArrayList<>();
        //待处理的变量
        Map<String, String> fieldNameMap = Arrays.stream(psiClass.getFields()).filter(f -> !currentMethodBodyStr.contains(COMMON_CONSTANT.THIS_STR + f.getName())).collect(Collectors.toMap(PsiField::getName, f -> f.getType().getInternalCanonicalText()));
        //构造方法的参数
        for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
            String parameterName = parameter.getName();
            if (parameter.getType().getInternalCanonicalText().equals(fieldNameMap.get(parameterName))) {
                addParameterList.add(COMMON_CONSTANT.THIS_STR + parameterName + COMMON_CONSTANT.EQ_STR + parameterName + COMMON_CONSTANT.SEMICOLON);
                fieldNameMap.remove(parameterName);
                continue;
            }
            PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameter.getType());
            if (null == parameterClass) {
                continue;
            }
            List<String> parameterClassMethodNameList = Arrays.stream(parameterClass.getMethods()).map(PsiMethod::getName).collect(Collectors.toList());
            //构造方法的参数为对象类型，处理对象的变量
            for (PsiField field : parameterClass.getFields()) {
                String fieldName = field.getName();
                String methodName = COMMON_CONSTANT.GET + StringUtil.toUpperCaseFirst(fieldName);
                if (!field.getType().getInternalCanonicalText().equals(fieldNameMap.get(fieldName)) || !parameterClassMethodNameList.contains(methodName)) {
                    continue;
                }
                addParameterList.add(COMMON_CONSTANT.THIS_STR + fieldName + COMMON_CONSTANT.EQ_STR + parameter.getName() + COMMON_CONSTANT.DOT + methodName + COMMON_CONSTANT.END_STR);
                fieldNameMap.remove(fieldName);
            }
        }
        String str = addParameterList.stream().filter(StringUtil::isNotEmpty).collect(Collectors.joining(COMMON_CONSTANT.BLANK_STRING));
        if (StringUtil.isNotEmpty(str)) {
            returnList.add(LookupElementBuilder.create(str).withPresentableText("fillConstructor").withInsertHandler((context, item) -> {
                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(currentMethod.getProject());
                codeStyleManager.reformat(currentMethod);
            }));
        }
        return returnList;
    }
}
