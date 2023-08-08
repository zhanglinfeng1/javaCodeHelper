package pers.zlf.plugin.completion;

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 14:18
 */
public class ConstructorCompletionContributor extends BaseCompletionContributor {
    /** 当前方法 */
    private PsiMethod currentMethod;
    /** 当前方法所在类 */
    private PsiClass currentMethodClass;

    @Override
    protected boolean check() {
        //当前光标所在的方法
        currentMethod = PsiTreeUtil.getParentOfType(currentElement, PsiMethod.class);
        if (null != currentMethod && currentMethod.isConstructor()) {
            this.currentMethodClass = currentMethod.getContainingClass();
            return null != currentMethodClass && MyPsiUtil.isNewLine(currentElement);
        }
        return false;
    }

    @Override
    protected void completion() {
        //已赋值字段
        List<String> assignedFieldList = getAssignedFieldList(currentMethod.getBody());
        //待赋值字段
        Map<String, String> fieldMap = MyPsiUtil.getPsiFieldList(currentMethodClass).stream().filter(f -> !assignedFieldList.contains(f.getName()))
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
            for (PsiField field : MyPsiUtil.getPsiFieldList(parameterClass)) {
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
            addCompletionResult(fillStr.toString(), Common.FILL_CONSTRUCTOR, (context, item) -> CodeStyleManager.getInstance(currentMethod.getProject()).reformat(currentMethod));
        }
    }

    private List<String> getAssignedFieldList(PsiCodeBlock codeBlock) {
        Function<PsiElement, List<PsiField>> function = element -> {
            if (!(element instanceof PsiExpressionStatement)) {
                return null;
            }
            PsiExpression expression = ((PsiExpressionStatement) element).getExpression();
            if (!(expression instanceof PsiAssignmentExpression)) {
                return null;
            }
            PsiExpression leftExpression = ((PsiAssignmentExpression) expression).getLExpression();
            if (!(leftExpression instanceof PsiReferenceExpression)) {
                return null;
            }
            PsiElement resolvedElement = ((PsiReferenceExpression) leftExpression).resolve();
            if (resolvedElement instanceof PsiField) {
                return List.of((PsiField) resolvedElement);
            }
            return null;
        };
        List<PsiField> assignedFieldList = MyPsiUtil.getElementFromPsiCodeBlock(codeBlock, function);
        return assignedFieldList.stream().map(PsiField::getName).collect(Collectors.toList());
    }
}
