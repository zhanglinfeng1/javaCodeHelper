package pers.zlf.plugin.pojo;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/28 10:42
 */
public class MappingAnnotation {
    /** 请求路径 */
    private String url;
    /** get、post、put、delete */
    private String method;
    /** 跳转注解 */
    private PsiAnnotation psiAnnotation;
    /** 跳转目标 */
    private List<PsiMethod> targetList;

    public MappingAnnotation() {
    }

    public MappingAnnotation(PsiAnnotation psiAnnotation, String url, String method) {
        this.psiAnnotation = psiAnnotation;
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

    public List<PsiMethod> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<PsiMethod> targetList) {
        this.targetList = targetList;
    }

    @Override
    public String toString() {
        return Arrays.stream(this.url.split(Common.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(Common.SLASH)) + Common.UNDERLINE + this.method;
    }
}
