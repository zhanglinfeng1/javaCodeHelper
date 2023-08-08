package pers.zlf.plugin.pojo.psi;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiParameter;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/29 13:01
 */
public class PsiParameterModel {
    private String name;
    private String type;

    public PsiParameterModel() {
    }

    public PsiParameterModel(PsiParameter parameter) {
        PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(parameter.getAnnotations(), List.of(Annotation.IBATIS_PARAM));
        this.name = null == psiAnnotation ? parameter.getName() : MyPsiUtil.getAnnotationValue(psiAnnotation, Annotation.VALUE);
        this.type = parameter.getType().getPresentableText();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
