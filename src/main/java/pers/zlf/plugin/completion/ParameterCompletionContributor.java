package pers.zlf.plugin.completion;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.tree.java.ParameterElement;
import com.intellij.psi.util.PsiTreeUtil;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 14:18
 */
public class ParameterCompletionContributor extends BaseCompletionContributor {
    /** 当前参数 */
    private PsiParameter parameter;

    @Override
    protected boolean check() {
        //当前光标所在的方法
        this.parameter = PsiTreeUtil.getParentOfType(currentElement, PsiParameter.class);
        if (currentElement.getParent() instanceof ParameterElement) {
            return null != parameter;
        }
        return false;
    }

    @Override
    protected void completion() {
        List<String> annotationNameList = List.of(Annotation.REQUEST_PARAM, Annotation.REQUEST_PART, Annotation.PATH_VARIABLE, Annotation.REQUEST_ATTRIBUTE, Annotation.REQUEST_HEADER, Annotation.IBATIS_PARAM);
        PsiAnnotation annotation = MyPsiUtil.findAnnotation(parameter.getAnnotations(), annotationNameList);
        if (null == annotation) {
            return;
        }
        String completionText = MyPsiUtil.getAnnotationValue(annotation, Annotation.VALUE);
        if (StringUtil.isEmpty(completionText)) {
            completionText = MyPsiUtil.getAnnotationValue(annotation, Annotation.NAME);
        }
        Empty.of(completionText).map(StringUtil::toHumpStyle).ifPresent(this::addCompletionResult);
    }
}
