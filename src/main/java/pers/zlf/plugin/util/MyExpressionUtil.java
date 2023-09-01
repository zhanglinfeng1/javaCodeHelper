package pers.zlf.plugin.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;
import pers.zlf.plugin.constant.Common;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 9:50
 */
public class MyExpressionUtil {

    /**
     * 只有一个参数时，返回参数名，有多个时，返回null
     *
     * @param expressionList PsiExpressionList
     * @return String
     */
    public static String getOnlyOneParameterName(PsiExpressionList expressionList) {
        if (null == expressionList) {
            return null;
        }
        PsiExpression[] expressionArr = expressionList.getExpressions();
        if (expressionArr.length == 0) {
            return Common.BLANK_STRING;
        }
        if (expressionArr.length == 1) {
            return expressionArr[0].getText();
        }
        return null;
    }

    /**
     * 获取类型名
     *
     * @param referenceExpression PsiReferenceExpression
     * @return String
     */
    public static String getTypeName(PsiReferenceExpression referenceExpression) {
        if (referenceExpression == null) {
            return null;
        }
        PsiElement element = Optional.ofNullable(referenceExpression.getReference()).map(PsiReference::resolve).orElse(null);
        if (element instanceof PsiVariable) {
            return ((PsiVariable) element).getType().getPresentableText();
        }
        return null;
    }

}
