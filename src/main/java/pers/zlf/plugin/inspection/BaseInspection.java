package pers.zlf.plugin.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.inspection.fix.ReplaceQuickFix;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 12:00
 */
public abstract class BaseInspection extends AbstractBaseJavaLocalInspectionTool {
    /**
     * 获取对象的声明信息
     *
     * @param variableExpression 引用表达式
     * @param quickFix 快速处理
     */
    protected final void simplifyDeclaration(PsiExpression variableExpression, ReplaceQuickFix quickFix) {
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
            String declarationLeftText = declarationText.substring(0, index);
            String declarationRightText = declarationText.substring(index + 1).trim();
            if (!declarationRightText.endsWith(Common.SEMICOLON)) {
                return;
            }
            declarationRightText = declarationRightText.substring(0, declarationRightText.length() - 1);
            if (StringUtil.isNotEmpty(declarationLeftText) && StringUtil.isNotEmpty(declarationRightText)) {
                quickFix.dealDeclarationInfo(declarationLeftText, declarationRightText);
            }
        }
    }
}
