package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.psi.PsiCodeBlock;
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
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.util.MyExpressionUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 12:00
 */
public abstract class BaseInspection extends AbstractBaseJavaLocalInspectionTool {
    /** 可以简化return */
    protected boolean canSimplifyReturn;
    /** return语句 */
    protected PsiReturnStatement returnStatement;
    /** 简化return的代码 */
    protected String simplifyReturnText;
    /** 调用的方法数量 */
    private int methodCallCount;
    /** 可以简化声明  */
    protected boolean canSimplifyDeclaration;
    /** 声明语句  */
    protected PsiElement declarationElement;
    /** 声明语句的左边代码 */
    protected String declarationLeftText;
    /** 声明语句的右边代码 */
    protected String declarationRightText;

    /**
     * 简化return
     *
     * @param element      当前元素
     * @param variableName 变量名
     */
    protected final void simplifyReturn(PsiElement element, String variableName) {
        canSimplifyReturn = false;
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
     */
    protected final void simplifyIntoLambda(PsiExpression expression, String variableName) {
        String elementText = Optional.ofNullable(expression).map(PsiElement::getText).orElse(Common.BLANK_STRING);
        String functionVariableName = Common.T;
        int count = 1;
        while (functionVariableName.equals(elementText)) {
            functionVariableName = Common.T + count;
            count++;
        }
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
            canSimplifyReturn = true;
        }
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

    /**
     * 获取对象的声明信息
     *
     * @param variableExpression 引用表达式
     */
    protected final void simplifyDeclaration(PsiExpression variableExpression) {
        canSimplifyDeclaration = false;
        if (!(variableExpression instanceof PsiReferenceExpression)) {
            return;
        }
        //判断对象
        PsiReferenceExpression variableReference = (PsiReferenceExpression) variableExpression;
        PsiElement variableElement = variableReference.resolve();
        if (!(variableElement instanceof PsiLocalVariable)) {
            return;
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
                return;
            }
            if (startOffset != 0 && (referenceOffset > endOffset || referenceOffset < startOffset)) {
                return;
            }
        }
        //获取声明语句
        PsiElement declaration = variable.getParent();
        if (declaration instanceof PsiDeclarationStatement) {
            PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) declaration;
            String declarationText = declarationStatement.getText();
            int index = declarationText.indexOf(Common.EQ_STR.trim());
            if (index == -1) {
                return;
            }
            declarationLeftText = declarationText.substring(0, index);
            declarationRightText = declarationText.substring(index + 1).trim();
            if (!declarationRightText.endsWith(Common.SEMICOLON)) {
                return;
            }
            declarationRightText = declarationRightText.substring(0, declarationRightText.length() - 1);
            if (StringUtil.isNotEmpty(declarationLeftText) && StringUtil.isNotEmpty(declarationRightText)) {
                canSimplifyDeclaration = true;
                declarationElement = declaration;
            }
        }
    }
}
