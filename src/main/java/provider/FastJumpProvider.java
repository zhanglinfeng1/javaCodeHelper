package provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import constant.ANNOTATION_CONSTANT;
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import org.jetbrains.annotations.NotNull;
import pojo.MappingAnnotation;
import util.JavaFileUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 18:08
 */
public class FastJumpProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            String fileType;
            if (JavaFileUtil.isFeign(psiClass)) {
                fileType = COMMON_CONSTANT.FEIGN;
            } else if (JavaFileUtil.isModuleController(psiClass)) {
                fileType = COMMON_CONSTANT.CONTROLLER;
            } else {
                return;
            }
            //获取方法的注解
            MappingAnnotation mappingAnnotation = getMappingAnnotation(psiMethod.getAnnotations());
            if (null == mappingAnnotation) {
                return;
            }
            //获取类的注解路径
            String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
            mappingAnnotation.setUrl(classUrl + mappingAnnotation.getUrl());
            //寻找对应方法
            List<PsiElement> elementList = new ArrayList<>();
            Project project = element.getProject();
            VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
            for (VirtualFile virtualFile : contentRoots) {
                if (virtualFile.getPath().contains("/src")) {
                    PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
                    if (null != psiDirectory) {
                        elementList.addAll(this.dealDirectory(psiDirectory, mappingAnnotation, fileType));
                    }
                }
            }
            if (elementList.isEmpty()) {
                return;
            }
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(ICON_CONSTANT.BO_LUO_SVG_16).setTargets(elementList).setTooltipText(COMMON_CONSTANT.BLANK_STRING);
            result.add(builder.createLineMarkerInfo(element));
        }
    }

    private List<PsiMethod> dealDirectory(PsiDirectory psiDirectory, MappingAnnotation mappingAnnotation, String fileType) {
        List<PsiMethod> methodList = new ArrayList<>();
        PsiDirectory[] subdirectories = psiDirectory.getSubdirectories();
        for (PsiDirectory subdirectory : subdirectories) {
            List<PsiMethod> subMethodList = this.dealDirectory(subdirectory, mappingAnnotation, fileType);
            if (!subMethodList.isEmpty()) {
                methodList.addAll(subMethodList);
            }
        }
        PsiFile[] files = psiDirectory.getFiles();
        for (PsiFile file : files) {
            // 不是 Java 类型的文件直接跳过
            if (!(file.getFileType() instanceof JavaFileType)) {
                break;
            }
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass[] psiClassArr = psiJavaFile.getClasses();
            int psiClassCount = psiClassArr.length;
            //含有内部类跳过
            if (psiClassCount == 0 || psiClassCount > 1) {
                break;
            }
            PsiClass psiClass = psiClassArr[0];
            // controller 跳feign 。必须是接口类型文件
            if (COMMON_CONSTANT.CONTROLLER.equals(fileType) && !psiClass.isInterface()) {
                break;
            }
            //原路径与目标路径文件不匹配
            if (!(JavaFileUtil.isModuleController(psiClass) && COMMON_CONSTANT.FEIGN.equals(fileType))
                    && !(JavaFileUtil.isFeign(psiClass) && COMMON_CONSTANT.CONTROLLER.equals(fileType))) {
                break;
            }
            //类注解路径
            String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                //获取方法的注解
                MappingAnnotation targetMappingAnnotation = getMappingAnnotation(psiMethod.getAnnotations());
                if (null == targetMappingAnnotation) {
                    break;
                }
                targetMappingAnnotation.setUrl(classUrl + targetMappingAnnotation.getUrl());
                if (mappingAnnotation.equals(targetMappingAnnotation)) {
                    methodList.add(psiMethod);
                }
            }
        }
        return methodList;
    }

    private MappingAnnotation getMappingAnnotation(PsiAnnotation[] psiAnnotationArr) {
        PsiAnnotation annotation = null;
        for (PsiAnnotation psiAnnotation : psiAnnotationArr) {
            if (ANNOTATION_CONSTANT.MAPPING_LIST.contains(psiAnnotation.getQualifiedName())) {
                annotation = psiAnnotation;
                break;
            }
        }
        if (null == annotation) {
            return null;
        }
        //方法注解
        String methodUrl = this.getMappingUrl(annotation);
        if (StringUtil.isEmpty(methodUrl)) {
            return null;
        }
        //请求方式
        String requestMethod = this.getMappingMethod(annotation);
        return new MappingAnnotation(methodUrl, requestMethod);
    }

    private String getMappingUrl(PsiAnnotation annotation) {
        if (null == annotation) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        String url = JavaFileUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.VALUE);
        if (StringUtil.isEmpty(url)) {
            return JavaFileUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.PATH);
        }
        return url;
    }

    private String getMappingMethod(PsiAnnotation annotation) {
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
                String method = JavaFileUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.METHOD);
                if (method.contains(COMMON_CONSTANT.DOT)) {
                    return method.substring(method.indexOf(COMMON_CONSTANT.DOT) + 1);
                }
                return JavaFileUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.METHOD);
        }
    }
}