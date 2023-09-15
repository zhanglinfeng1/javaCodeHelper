package pers.zlf.plugin.util;

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.pojo.Count;
import pers.zlf.plugin.pojo.SimplifyInfo;

import java.util.Optional;
import java.util.function.Predicate;

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
     * 获取和null比较的变量
     *
     * @param binaryExpression 比较表达式
     * @return PsiExpression
     */
    public static PsiExpression getExpressionComparedToNull(PsiBinaryExpression binaryExpression) {
        PsiExpression leftOperand = binaryExpression.getLOperand();
        PsiExpression rightOperand = binaryExpression.getROperand();
        Predicate<PsiExpression> isNull = t -> (t instanceof PsiLiteralExpressionImpl && ((PsiLiteralExpressionImpl) t).getLiteralElementType() == JavaTokenType.NULL_KEYWORD);
        if (isNull.test(leftOperand)) {
            return rightOperand;
        } else if (isNull.test(rightOperand)) {
            return leftOperand;
        }
        return null;
    }

    /**
     * 简化return
     *
     * @param element      当前元素
     * @param variableName 变量名
     * @return SimplifyInfo
     */
    public static SimplifyInfo simplifyReturn(PsiElement element, String variableName) {
        //简化return
        if (element instanceof PsiReturnStatement) {
            PsiReturnStatement returnStatement = (PsiReturnStatement) element;
            PsiExpression expression = returnStatement.getReturnValue();
            return simplifyIntoLambda(expression, variableName);
        }
        return new SimplifyInfo();
    }

    /**
     * 简化成lambda表达式
     *
     * @param expression   语句
     * @param variableName 变量名
     * @return SimplifyInfo
     */
    public static SimplifyInfo simplifyIntoLambda(PsiExpression expression, String variableName) {
        SimplifyInfo simplifyInfo = new SimplifyInfo();
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        String functionVariableName = Common.T;
        int count = 1;
        while (functionVariableName.equals(elementText)) {
            functionVariableName = Common.T + count;
            count++;
        }
        String simplifyReturnText = null;
        if (expression instanceof PsiReferenceExpression) {
            simplifyReturnText = variableName.equals(elementText) ? Common.BLANK_STRING : String.format(Common.MAP_COMMON_STR, functionVariableName, elementText);
        } else if (expression instanceof PsiLiteralExpression) {
            simplifyReturnText = String.format(Common.MAP_COMMON_STR, functionVariableName, elementText);
        } else if (expression instanceof PsiNewExpression) {
            simplifyReturnText = simplifyNew((PsiNewExpression) expression, variableName);
        } else if (expression instanceof PsiMethodCallExpression) {
            simplifyReturnText = simplifyMethodCall((PsiMethodCallExpression) expression, variableName);
        }
        if (simplifyReturnText != null) {
            simplifyInfo.setSimplifyReturn(true);
            simplifyInfo.setSimplifyReturnText(simplifyReturnText);
        }
        return simplifyInfo;
    }

    /**
     * 简化new语句
     *
     * @param newExpression new语句
     * @param variableName  变量名
     * @return String
     */
    public static String simplifyNew(PsiNewExpression newExpression, String variableName) {
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
    public static String simplifyMethodCall(PsiMethodCallExpression methodCallExpression, String variableName) {
        //方法参数
        String parameterText = MyExpressionUtil.getOnlyOneParameterName(methodCallExpression.getArgumentList());
        if (parameterText == null) {
            return null;
        }
        if (parameterText.equals(variableName)) {
            parameterText = Common.T;
        }
        Count count = new Count();
        count.add();
        //顶层方法的父类
        PsiReferenceExpression referenceExpression = getReferenceElement(methodCallExpression, count);
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
        if (count.getNum() == 1) {
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

    private static PsiReferenceExpression getReferenceElement(PsiMethodCallExpression methodCallExpression, Count count) {
        PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
        for (PsiElement element : referenceExpression.getChildren()) {
            if (element instanceof PsiMethodCallExpression) {
                count.add();
                return getReferenceElement((PsiMethodCallExpression) element, count);
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
     * 获取对象的声明信息
     *
     * @param variableExpression 引用表达式
     * @return SimplifyInfo
     */
    public static SimplifyInfo simplifyDeclaration(PsiExpression variableExpression) {
        SimplifyInfo simplifyInfo = new SimplifyInfo();
        if (!(variableExpression instanceof PsiReferenceExpression)) {
            return simplifyInfo;
        }
        //判断对象
        PsiReferenceExpression variableReference = (PsiReferenceExpression) variableExpression;
        PsiElement variableElement = variableReference.resolve();
        if (!(variableElement instanceof PsiLocalVariable)) {
            return simplifyInfo;
        }
        int currentOffset = variableReference.getTextOffset();
        PsiCodeBlock psiCodeBlock = PsiTreeUtil.getParentOfType(variableReference, PsiCodeBlock.class);
        int startOffset = Optional.ofNullable(psiCodeBlock).map(PsiCodeBlock::getTextOffset).orElse(0);
        int endOffset = startOffset + Optional.ofNullable(psiCodeBlock).map(PsiCodeBlock::getTextLength).orElse(0);
        PsiLocalVariable variable = (PsiLocalVariable) variableElement;
        //存在其他引用、作用域不同 则返回
        for (PsiReference reference : ReferencesSearch.search(variable).toArray(new PsiReference[0])) {
            int referenceOffset = reference.getAbsoluteRange().getEndOffset();
            if (referenceOffset < currentOffset) {
                return simplifyInfo;
            }
            if (startOffset != 0 && (referenceOffset > endOffset || referenceOffset < startOffset)) {
                return simplifyInfo;
            }
        }
        //获取声明语句
        PsiElement declaration = variable.getParent();
        if (declaration instanceof PsiDeclarationStatement) {
            PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) declaration;
            String declarationText = declarationStatement.getText();
            int index = declarationText.indexOf(Common.EQ_STR.trim());
            if (index == -1) {
                return simplifyInfo;
            }
            String declarationLeftText = declarationText.substring(0, index);
            String declarationRightText = declarationText.substring(index + 1).trim();
            if (!declarationRightText.endsWith(Common.SEMICOLON)) {
                return simplifyInfo;
            }
            declarationRightText = declarationRightText.substring(0, declarationRightText.length() - 1);
            if (StringUtil.isNotEmpty(declarationLeftText) && StringUtil.isNotEmpty(declarationRightText)) {
                simplifyInfo.setSimplifyDeclaration(true);
                simplifyInfo.setDeclarationLeftText(declarationLeftText);
                simplifyInfo.setDeclarationRightText(declarationRightText);
            }
        }
        return simplifyInfo;
    }
}
