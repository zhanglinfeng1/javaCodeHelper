package pers.zlf.plugin.completion.code;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends BaseCompletion {

    public ConstructorCompletion(PsiMethod currentMethod, PsiElement psiElement) {
        super(currentMethod, psiElement);
    }

    @Override
    public void init() {
        if (!isNewLine) {
            return;
        }
        //已赋值字段
        List<String> assignedFieldList = MyPsiUtil.getPsiFieldList(currentMethod.getBody()).stream().map(PsiField::getName).collect(Collectors.toList());
        //待赋值字段
        Map<String, String> fieldMap = Arrays.stream(currentMethodClass.getFields()).filter(f -> !assignedFieldList.contains(f.getName()))
                .collect(Collectors.toMap(PsiField::getName, f -> f.getType().getInternalCanonicalText()));
        //构造方法的参数
        StringBuilder fillStr = new StringBuilder();
        loop:
        for (PsiParameter parameter : currentMethod.getParameterList().getParameters()) {
            if (fieldMap.isEmpty()) {
                break;
            }
            String parameterName = parameter.getName();
            PsiType parameterType = parameter.getType();
            if (parameterType.getInternalCanonicalText().equals(fieldMap.get(parameterName))) {
                fillStr.append(String.format(Common.CONSTRUCTOR_FILL_STR1, parameterName, parameterName));
                fieldMap.remove(parameterName);
                continue;
            }
            PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameterType);
            if (null == parameterClass) {
                continue;
            }
            //构造方法的参数为对象类型，处理对象的变量
            for (PsiField field : parameterClass.getFields()) {
                if (fieldMap.isEmpty()) {
                    break loop;
                }
                String fieldName = field.getName();
                if (field.getType().getInternalCanonicalText().equals(fieldMap.get(fieldName))) {
                    fillStr.append(String.format(Common.CONSTRUCTOR_FILL_STR2, fieldName, parameterName, StringUtil.toUpperCaseFirst(fieldName)));
                    fieldMap.remove(fieldName);
                }
            }
        }
        if (StringUtil.isNotEmpty(fillStr)) {
            returnList.add(LookupElementBuilder.create(fillStr.toString()).withPresentableText(Common.FILL_CONSTRUCTOR)
                    .withInsertHandler((context, item) -> CodeStyleManager.getInstance(currentMethod.getProject()).reformat(currentMethod)));
        }
    }
}
