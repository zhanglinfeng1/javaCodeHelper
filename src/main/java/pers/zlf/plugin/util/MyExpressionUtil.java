package pers.zlf.plugin.util;

import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.searches.ReferencesSearch;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.psi.PsiDeclarationStatementModel;

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

    /**
     * 获取对象的声明信息
     *
     * @param variableExpression 引用表达式
     * @return PsiDeclarationStatementModel
     */
    public static PsiDeclarationStatementModel getDeclarationModel(PsiExpression variableExpression) {
        if (!(variableExpression instanceof PsiReferenceExpression)) {
            return null;
        }
        //判断对象
        PsiReferenceExpression variableReference = (PsiReferenceExpression) variableExpression;
        PsiElement variableElement = variableReference.resolve();
        if (!(variableElement instanceof PsiLocalVariable)) {
            return null;
        }
        int currentOffset = variableReference.getAbsoluteRange().getEndOffset();
        PsiLocalVariable variable = (PsiLocalVariable) variableElement;
        //存在其他引用则返回
        for (PsiReference reference : ReferencesSearch.search(variable).toArray(new PsiReference[0])) {
            if (reference.getAbsoluteRange().getEndOffset() < currentOffset) {
                return null;
            }
        }
        //获取声明语句
        PsiElement declaration = variable.getParent();
        if (declaration instanceof PsiDeclarationStatement) {
            PsiDeclarationStatementModel declarationModel = new PsiDeclarationStatementModel((PsiDeclarationStatement) declaration);
            if (declarationModel.exist()) {
                declarationModel.setDeclaration(declaration);
                return declarationModel;
            }
        }
        return null;
    }
}
