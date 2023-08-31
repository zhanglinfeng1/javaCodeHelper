package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.searches.ReferencesSearch;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.psi.PsiDeclarationStatementModel;
import pers.zlf.plugin.util.MyExpressionUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 12:00
 */
public abstract class BaseInspection extends AbstractBaseJavaLocalInspectionTool {
    /** return语句 */
    protected PsiReturnStatement returnStatement;
    /** 可以简化 */
    protected boolean isSimplify;
    /** 简化后的代码 */
    protected String simplifyText;
    /** 声明语句  */
    protected PsiElement declaration;

    /**
     * 简化return
     *
     * @param element      当前元素
     * @param variableName 变量名
     * @return String
     */
    protected final void simplifyReturn(PsiElement element, String variableName) {
        returnStatement = null;
        PsiElement nextElement = getNextElement(element);
        //简化return
        if (nextElement instanceof PsiReturnStatement) {
            returnStatement = (PsiReturnStatement) nextElement;
            PsiExpression expression = returnStatement.getReturnValue();
            simplifyIntoLambda(expression, variableName);
        }
    }

    /**
     * 简化成lambda表达式
     *
     * @param expression   语句
     * @param variableName 变量名
     * @return String
     */
    protected final void simplifyIntoLambda(PsiExpression expression, String variableName) {
        isSimplify = false;
        simplifyText = null;
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        if (expression instanceof PsiReferenceExpression) {
            isSimplify = true;
            simplifyText = variableName.equals(elementText) ? Common.BLANK_STRING : String.format(Common.MAP_COMMON_STR, elementText);
        } else if (expression instanceof PsiLiteralExpression) {
            isSimplify = true;
            simplifyText = String.format(Common.MAP_COMMON_STR, elementText);
        } else if (expression instanceof PsiNewExpression) {
            //返回 new 对象
            PsiNewExpression newExpression = (PsiNewExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(newExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                Optional<String> opt = Optional.ofNullable(newExpression.getClassReference())
                        .map(PsiJavaCodeReferenceElement::getReferenceName)
                        .map(t -> String.format(Common.MAP_LAMBDA_STR, t, Keyword.JAVA_NEW));
                if (opt.isPresent()) {
                    isSimplify = true;
                    simplifyText = opt.get();
                }
            }
        } else if (expression instanceof PsiMethodCallExpression) {
            //调用方法
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
            String parameterName = MyExpressionUtil.getOnlyOneParameterName(methodCallExpression.getArgumentList());
            if (parameterName.equals(variableName)) {
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                String[] expressionTextArr = referenceExpression.getQualifiedName().split(Regex.DOT);
                if (expressionTextArr.length == 1) {
                    isSimplify = true;
                    simplifyText = String.format(Common.MAP_LAMBDA_STR, Keyword.JAVA_THIS, expressionTextArr[0]);
                } else if (expressionTextArr.length == 2) {
                    isSimplify = true;
                    simplifyText = String.format(Common.MAP_LAMBDA_STR, expressionTextArr[0], expressionTextArr[1]);
                }
            }
        }
    }

    /**
     * 获取下一个元素，除空格
     *
     * @param element 当前元素
     * @return PsiElement
     */
    protected final PsiElement getNextElement(PsiElement element) {
        PsiElement nextElement = element.getNextSibling();
        if (nextElement instanceof PsiWhiteSpace) {
            return getNextElement(nextElement);
        }
        return nextElement;
    }

    /**
     * 获取对象的声明信息
     *
     * @param variableExpression 引用表达式
     * @return PsiDeclarationStatementModel
     */
    protected final PsiDeclarationStatementModel getDeclarationModel(PsiExpression variableExpression) {
        declaration = null;
        if (!(variableExpression instanceof PsiReferenceExpression)) {
            return null;
        }
        //判断为空的对象
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
        declaration = variable.getParent();
        if (declaration instanceof PsiDeclarationStatement) {
            PsiDeclarationStatementModel declarationModel = new PsiDeclarationStatementModel((PsiDeclarationStatement) declaration);
            if (declarationModel.exist()) {
                return declarationModel;
            }
        }
        return null;
    }
}
