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
import com.intellij.psi.PsiWhiteSpace;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.pojo.SimplifyInfo;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 12:00
 */
public abstract class BaseInspection extends AbstractBaseJavaLocalInspectionTool {
    private int methodCallCount;

    /**
     * 简化return
     *
     * @param element      当前元素
     * @param variableName 变量名
     * @return SimplifyInfo
     */
    protected final SimplifyInfo simplifyReturn(PsiElement element, String variableName) {
        PsiElement nextElement = getNextElement(element);
        //简化return
        if (nextElement instanceof PsiReturnStatement) {
            PsiReturnStatement returnStatement = (PsiReturnStatement) nextElement;
            PsiExpression expression = returnStatement.getReturnValue();
            SimplifyInfo simplifyInfo = simplifyIntoLambda(expression, variableName);
            if (simplifyInfo != null) {
                simplifyInfo.setReturnStatement(returnStatement);
            }
            return simplifyInfo;
        }
        return null;
    }

    /**
     * 简化成lambda表达式
     *
     * @param expression   语句
     * @param variableName 变量名
     * @return SimplifyInfo
     */
    protected final SimplifyInfo simplifyIntoLambda(PsiExpression expression, String variableName) {
        SimplifyInfo simplifyInfo = new SimplifyInfo();
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        String functionVariableName = Common.T;
        int count = 1;
        while (functionVariableName.equals(elementText)) {
            functionVariableName = Common.T + count;
            count++;
        }
        if (expression instanceof PsiReferenceExpression) {
            simplifyInfo.setSimplify(true);
            simplifyInfo.setSimplifyText(variableName.equals(elementText) ? Common.BLANK_STRING : String.format(Common.MAP_COMMON_STR, functionVariableName, elementText));
            return simplifyInfo;
        } else if (expression instanceof PsiLiteralExpression) {
            simplifyInfo.setSimplifyText(String.format(Common.MAP_COMMON_STR, functionVariableName, elementText));
        } else if (expression instanceof PsiNewExpression) {
            simplifyInfo.setSimplifyText(simplifyNew((PsiNewExpression) expression, variableName));
        } else if (expression instanceof PsiMethodCallExpression) {
            simplifyInfo.setSimplifyText(simplifyMethodCall((PsiMethodCallExpression) expression, variableName));
        }
        if (StringUtil.isNotEmpty(simplifyInfo.getSimplifyText())) {
            simplifyInfo.setSimplify(true);
            return simplifyInfo;
        }
        return null;
    }

    /**
     * 简化new语句
     *
     * @param newExpression new语句
     * @param variableName  变量名
     * @return String
     */
    protected String simplifyNew(PsiNewExpression newExpression, String variableName) {
        PsiJavaCodeReferenceElement referenceElement = newExpression.getClassReference();
        if (referenceElement == null) {
            return null;
        }
        String parameterText = MyExpressionUtil.getOnlyOneParameterName(newExpression.getArgumentList());
        if (variableName.equals(parameterText)) {
            return String.format(Common.MAP_LAMBDA_STR, referenceElement.getReferenceName(), Keyword.JAVA_NEW);
        } else if (Common.BLANK_STRING.equals(parameterText)) {
            String text = newExpression.getText();
            return String.format(Common.MAP_COMMON_STR, Common.T, text);
        }
        return null;
    }

    /**
     * 简化方法调用
     *
     * @param methodCallExpression 调用语句
     * @param variableName         变量名
     * @return String
     */
    protected String simplifyMethodCall(PsiMethodCallExpression methodCallExpression, String variableName) {
        //方法参数
        String parameterText = MyExpressionUtil.getOnlyOneParameterName(methodCallExpression.getArgumentList());
        if (parameterText == null) {
            return null;
        }
        if (parameterText.equals(variableName)) {
            parameterText = Common.T;
        }
        methodCallCount = 1;
        //顶层方法的父类
        PsiReferenceExpression referenceExpression = getReferenceElement(methodCallExpression);
        //父类名
        String referenceName = Optional.ofNullable(referenceExpression).map(PsiElement::getText).orElse(Keyword.JAVA_THIS);
        if (referenceName.equals(variableName)) {
            referenceName = variableName;
        }
        String methodText = methodCallExpression.getText();
        methodText = methodText.replace(referenceName + Common.DOT, Common.BLANK_STRING);
        if (methodText.contains(Common.LEFT_PARENTHESES)) {
            methodText = methodText.substring(0, methodText.indexOf(Common.LEFT_PARENTHESES) + 1);
        }
        if (methodCallCount == 1) {
            if (parameterText.equals(Common.T)) {
                return String.format(Common.MAP_LAMBDA_STR, referenceName, methodText);
            }
            String variableTypeName = MyExpressionUtil.getTypeName(referenceExpression);
            if (referenceName.equals(variableName) && StringUtil.isNotEmpty(variableTypeName)) {
                return String.format(Common.MAP_LAMBDA_STR, variableTypeName, methodText);
            }
        }
        return String.format(Common.MAP_COMMON_STR, Common.T, methodText + parameterText + Common.RIGHT_PARENTHESES);
    }

    private PsiReferenceExpression getReferenceElement(PsiMethodCallExpression methodCallExpression) {
        PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
        for (PsiElement element : referenceExpression.getChildren()) {
            if (element instanceof PsiMethodCallExpression) {
                methodCallCount++;
                return getReferenceElement((PsiMethodCallExpression) element);
            }
        }
        for (PsiElement element : referenceExpression.getChildren()) {
            if (element instanceof PsiReferenceExpression) {
                return (PsiReferenceExpression) element;
            }
        }
        return null;
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

}
