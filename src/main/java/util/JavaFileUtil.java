package util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiUtil;
import constant.ANNOTATION_CONSTANT;
import constant.COMMON_CONSTANT;

import java.util.Arrays;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 16:25
 */
public class JavaFileUtil {

    public static boolean isFeign(PsiClass psiClass) {
        if (null == psiClass) {
            return false;
        }
        if (!psiClass.isInterface()) {
            return false;
        }
        return psiClass.getAnnotation(ANNOTATION_CONSTANT.OPEN_FEIGN_CLIENT) != null || psiClass.getAnnotation(ANNOTATION_CONSTANT.NETFLIX_FEIGN_CLIENT) != null;
    }

    public static boolean isModuleController(PsiClass psiClass) {
        PsiAnnotation[] psiAnnotationArr = psiClass.getAnnotations();
        if (psiAnnotationArr.length == 0) {
            return false;
        }
        if (psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return false;
        }
        //属于controller
        boolean isController = Arrays.stream(psiAnnotationArr).anyMatch(a -> ANNOTATION_CONSTANT.CONTROLLER_LIST.contains(a.getQualifiedName()));
        if (isController) {
            //排除网关
            return Arrays.stream(psiClass.getFields()).noneMatch(f -> isFeign(PsiUtil.resolveClassInClassTypeOnly(f.getType())));
        }
        return false;
    }

    public static String getAnnotationValue(PsiAnnotation annotation, String attributeName) {
        if (null == annotation) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        PsiAnnotationMemberValue value = annotation.findAttributeValue(attributeName);
        if (value == null) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        String attributeValue = value.getText();
        if (attributeValue.startsWith(COMMON_CONSTANT.LEFT_BRACE)) {
            attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
        }
        return attributeValue.replaceAll(COMMON_CONSTANT.DOUBLE_QUOTES_REGEX, COMMON_CONSTANT.BLANK_STRING);
    }

}
