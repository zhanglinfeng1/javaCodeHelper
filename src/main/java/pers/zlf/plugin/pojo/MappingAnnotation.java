package pers.zlf.plugin.pojo;

import com.intellij.psi.PsiAnnotation;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/28 10:42
 */
public class MappingAnnotation {
    private String url;
    private String method;
    private PsiAnnotation psiAnnotation;
    private List<PsiAnnotation> targetList;

    public MappingAnnotation() {
    }

    public MappingAnnotation(PsiAnnotation psiMethod, String url, String method) {
        this.psiAnnotation = psiMethod;
        this.url = url;
        this.method = method;
        this.targetList = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PsiAnnotation getPsiAnnotation() {
        return psiAnnotation;
    }

    public void setPsiAnnotation(PsiAnnotation psiAnnotation) {
        this.psiAnnotation = psiAnnotation;
    }

    public List<PsiAnnotation> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<PsiAnnotation> targetList) {
        this.targetList = targetList;
    }

    @Override
    public String toString() {
        return Arrays.stream(this.url.split(COMMON.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(COMMON.SLASH)) + COMMON.UNDERSCORE + this.method;
    }
}
