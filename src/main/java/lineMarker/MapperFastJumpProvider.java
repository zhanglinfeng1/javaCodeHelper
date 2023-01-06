package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.PsiShortNamesCache;
import constant.ANNOTATION;
import constant.COMMON;
import constant.ICON;
import constant.TYPE;
import org.jetbrains.annotations.NotNull;
import util.MyPsiUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/6 14:25
 */
public class MapperFastJumpProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            if (!psiClass.isInterface()) {
                return;
            }
            loop:
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                PsiAnnotation[] annotations = psiMethod.getAnnotations();
                Optional<PsiAnnotation> annotationOptional = Arrays.stream(annotations).filter(a -> null != a.getQualifiedName() && ANNOTATION.IBATIS_PROVIDER_LIST.contains(a.getQualifiedName())).findAny();
                if (annotationOptional.isPresent()) {
                    PsiAnnotation annotation = annotationOptional.get();
                    String className = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.TYPE).replace(TYPE.CLASS_FILE_SUFFIX, COMMON.BLANK_STRING);
                    String method = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.METHOD);
                    for (PsiClass targetClass : PsiShortNamesCache.getInstance(psiClass.getProject()).getClassesByName(className, psiClass.getResolveScope())) {
                        for (PsiMethod targetMethod : targetClass.getMethods()) {
                            if (method.equals(targetMethod.getName())) {
                                result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(targetMethod).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(psiMethod));
                                result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(psiMethod).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(targetMethod));
                                continue loop;
                            }
                        }
                    }
                }
            }
        }
    }
}
