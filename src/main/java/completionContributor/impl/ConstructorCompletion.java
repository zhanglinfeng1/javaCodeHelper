package completionContributor.impl;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends BasicCompletion {

    public ConstructorCompletion(PsiMethod currentMethod, CompletionParameters parameters, PsiElement psiElement) {
        super(currentMethod);
        //当前光标在新的一行
        Document document = parameters.getEditor().getDocument();
        int lineNum = document.getLineNumber(parameters.getOffset());
        int lineStart = document.getLineStartOffset(lineNum);
        int lineEnd = document.getLineEndOffset(lineNum);
        String lineText = document.getText(TextRange.create(lineStart, lineEnd));
        lineText = lineText.replaceAll(COMMON_CONSTANT.WRAP_REGEX, COMMON_CONSTANT.BLANK_STRING).trim();
        if (StringUtil.isNotEmpty(lineText) && lineText.length() > psiElement.getText().trim().length()) {
            this.currentMethod = null;
        }
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        if (null == currentMethod) {
            return returnList;
        }
        PsiClass psiClass = currentMethod.getContainingClass();
        if (null == psiClass) {
            return returnList;
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
            returnList.add(LookupElementBuilder.create(str).withPresentableText("fillConstructor").withInsertHandler((context, item) -> {
                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(currentMethod.getProject());
                codeStyleManager.reformat(currentMethod);
            }));
        }
        return returnList;
    }

}
