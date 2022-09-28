package util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import constant.ANNOTATION_CONSTANT;
import constant.COMMON_CONSTANT;
import pojo.MappingAnnotation;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 16:25
 */
public class JavaFileUtil {

    public static boolean isFeign(PsiElement psiElement) {
        PsiClass psiClass;
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            psiClass = (PsiClass) psiMethod.getParent();
        } else if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) psiElement;
        } else {
            return false;
        }
        return psiClass.getAnnotation(ANNOTATION_CONSTANT.OPEN_FEIGN_CLIENT) != null || psiClass.getAnnotation(ANNOTATION_CONSTANT.NETFLIX_FEIGN_CLIENT) != null;
    }

    public static boolean isModuleController(PsiElement psiElement) {
        PsiClass psiClass;
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            psiClass = (PsiClass) psiMethod.getParent();
        } else if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) psiElement;
        } else {
            return false;
        }
        //属于controller
        Optional<PsiAnnotation> annotationOpt = ANNOTATION_CONSTANT.CONTROLLER_LIST.stream().map(psiClass::getAnnotation).filter(Objects::nonNull).findAny();
        if (annotationOpt.isPresent()) {
            //排除网关的controller
            Optional<PsiField> fieldOpt = Arrays.stream(psiClass.getFields()).filter(f -> {
                String fieldTypeClassName = f.getType().getCanonicalText();
                Optional<PsiClass> fieldClassOptional = JavaFileUtil.findClazz(psiClass.getProject(), fieldTypeClassName);
                if (fieldClassOptional.isPresent()) {
                    PsiClass fieldClass = fieldClassOptional.get();
                    return isFeign(fieldClass);
                }
                return false;
            }).findAny();
            return fieldOpt.isEmpty();
        }
        return false;
    }

    public static MappingAnnotation getMappingAnnotation(PsiElement psiElement) {
        PsiMethod psiMethod = (PsiMethod) psiElement;
        // 获取注解
        Optional<PsiAnnotation> annotationOpt = ANNOTATION_CONSTANT.MAPPING_LIST.stream().map(psiMethod::getAnnotation).filter(Objects::nonNull).findAny();
        if (annotationOpt.isPresent()) {
            //方法注解
            PsiAnnotation annotation = annotationOpt.get();
            String url = getMappingUrl(annotation);
            if (StringUtil.isEmpty(url)) {
                return null;
            }
            String method = getMappingMethod(annotation);
            //类注解
            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            PsiAnnotation parentAnnotation = psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING);
            if (null == parentAnnotation) {
                return new MappingAnnotation(url, method);
            }
            String parentUrl = getMappingUrl(parentAnnotation);
            if (StringUtil.isEmpty(parentUrl)) {
                return new MappingAnnotation(url, method);
            }
            return new MappingAnnotation(parentUrl + url, method);
        }
        return null;
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

    public static String getMappingUrl(PsiAnnotation annotation) {
        String url = getAnnotationValue(annotation, ANNOTATION_CONSTANT.VALUE);
        if (StringUtil.isEmpty(url)) {
            return getAnnotationValue(annotation, ANNOTATION_CONSTANT.PATH);
        }
        return url;
    }

    public static String getMappingMethod(PsiAnnotation annotation) {
        switch (Objects.requireNonNull(annotation.getQualifiedName())) {
            case ANNOTATION_CONSTANT.POST_MAPPING:
                return COMMON_CONSTANT.POST;
            case ANNOTATION_CONSTANT.PUT_MAPPING:
                return COMMON_CONSTANT.PUT;
            case ANNOTATION_CONSTANT.GET_MAPPING:
                return COMMON_CONSTANT.GET;
            case ANNOTATION_CONSTANT.DELETE_MAPPING:
                return COMMON_CONSTANT.DELETE;
            default:
                String method = getAnnotationValue(annotation, ANNOTATION_CONSTANT.METHOD);
                if (method.contains(COMMON_CONSTANT.DOT)) {
                    return method.substring(method.indexOf(COMMON_CONSTANT.DOT) + 1);
                }
                return getAnnotationValue(annotation, ANNOTATION_CONSTANT.METHOD);
        }
    }

    public static Optional<PsiClass> findClazz(Project project, String clazzName) {
        String classNameNeedFind = clazzName;
        if (classNameNeedFind.contains("$")) {
            classNameNeedFind = classNameNeedFind.replace(COMMON_CONSTANT.$, COMMON_CONSTANT.DOT);
        }
        final JavaPsiFacade instance = JavaPsiFacade.getInstance(project);
        return Optional.ofNullable(instance.findClass(classNameNeedFind, GlobalSearchScope.allScope(project)));
    }
}
