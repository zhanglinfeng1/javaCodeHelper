package pojo;

import com.intellij.psi.PsiMethod;
import constant.COMMON;
import util.StringUtil;

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
    private PsiMethod psiMethod;
    private List<PsiMethod> targetMethodList;

    public MappingAnnotation() {
    }

    public MappingAnnotation(PsiMethod psiMethod, String url, String method) {
        this.psiMethod = psiMethod;
        this.url = url;
        this.method = method;
        this.targetMethodList = new ArrayList<>();
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

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public List<PsiMethod> getTargetMethodList() {
        return targetMethodList;
    }

    public void setTargetMethodList(List<PsiMethod> targetMethodList) {
        this.targetMethodList = targetMethodList;
    }

    @Override
    public String toString() {
        return Arrays.stream(this.url.split(COMMON.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(COMMON.SLASH)) + COMMON.UNDERSCORE + this.method;
    }
}
