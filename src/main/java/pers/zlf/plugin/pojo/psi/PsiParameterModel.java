package pers.zlf.plugin.pojo.psi;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiParameter;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.Arrays;
import java.util.Optional;

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
        Optional<PsiAnnotation> annotationOptional = Arrays.stream(parameter.getAnnotations()).filter(a -> Annotation.IBATIS_PARAM.equals(a.getQualifiedName())).findAny();
        if (annotationOptional.isPresent()) {
            this.name = MyPsiUtil.getAnnotationValue(annotationOptional.get(), Annotation.VALUE);
        } else {
            this.name = parameter.getName();
        }
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
