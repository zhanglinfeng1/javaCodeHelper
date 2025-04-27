package pers.zlf.plugin.pojo;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param url           请求路径
 * @param method        get、post、put、delete
 * @param psiAnnotation 跳转注解
 * @param targetList    跳转目标
 * @author zhanglinfeng
 * @date create in 2022/9/28 10:42
 */
public record MappingAnnotation(String url, String method, PsiAnnotation psiAnnotation, List<PsiMethod> targetList) {

    @Override
    public @NotNull String toString() {
        return Arrays.stream(this.url.split(Common.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(Common.SLASH)) + Common.UNDERLINE + this.method;
    }
}
