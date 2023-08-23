package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.MyExpressionUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 12:00
 */
public abstract class BaseInspection extends AbstractBaseJavaLocalInspectionTool {

    /**
     * 简化return
     *
     * @param returnStatement PsiReturnStatement
     * @param variableName    变量名
     * @param quickFix        快速解决方案
     * @return String
     */
    protected final String simplifyReturn(PsiReturnStatement returnStatement, String variableName, ReplaceQuickFix quickFix) {
        PsiExpression expression = returnStatement.getReturnValue();
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        String result = Common.BLANK_STRING;
        //返回单个变量或常量
        if (expression instanceof PsiReferenceExpression || expression instanceof PsiLiteralExpression) {
            result = variableName.equals(elementText) ? Common.BLANK_STRING : String.format(Common.MAP_COMMON_STR, elementText);
        } else if (expression instanceof PsiNewExpression) {
            //返回 new 对象
            PsiNewExpression newExpression = (PsiNewExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(newExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                result = Optional.ofNullable(newExpression.getClassReference())
                        .map(PsiJavaCodeReferenceElement::getReferenceName)
                        .map(t -> String.format(Common.MAP_LAMBDA_STR, t, Keyword.JAVA_NEW.toUpperCase()))
                        .orElse(null);
            }
        } else if (expression instanceof PsiMethodCallExpression) {
            //调用方法
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(methodCallExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                String[] expressionTextArr = referenceExpression.getQualifiedName().split(Regex.DOT);
                if (expressionTextArr.length == 1) {
                    result = String.format(Common.MAP_LAMBDA_STR, Keyword.JAVA_THIS, expressionTextArr[0]);
                } else if (expressionTextArr.length == 2) {
                    result = String.format(Common.MAP_LAMBDA_STR, expressionTextArr[0], expressionTextArr[1]);
                }
            }
        }
        if (result == null) {
            result = Common.BLANK_STRING;
        } else {
            quickFix.setText(Keyword.JAVA_RETURN + Common.SPACE);
            quickFix.addFixRunnable(returnStatement::delete);
        }
        return result;
    }
}
