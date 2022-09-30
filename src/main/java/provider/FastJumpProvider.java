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
import constant.COMMON_CONSTANT;
import constant.ICON_CONSTANT;
import org.jetbrains.annotations.NotNull;
import pojo.MappingAnnotation;
import util.JavaFileUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            PsiAnnotation[] psiAnnotationArr = psiClass.getAnnotations();
            if (psiAnnotationArr.length == 0) {
                return;
            }
            String fileType;
            if (JavaFileUtil.isFeign(psiAnnotationArr)) {
                fileType = COMMON_CONSTANT.FEIGN;
            } else if (JavaFileUtil.isModuleController(psiClass, psiAnnotationArr)) {
                fileType = COMMON_CONSTANT.CONTROLLER;
            } else {
                return;
            }
            //获取注解路径
            MappingAnnotation mappingAnnotation = JavaFileUtil.getMappingAnnotation(psiMethod);
            if (null == mappingAnnotation) {
                return;
            }
            //寻找对应方法
            List<PsiElement> elementList = this.getTargetArr(psiMethod.getProject(), mappingAnnotation, fileType);
            if (elementList.isEmpty()) {
                return;
            }
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(ICON_CONSTANT.BO_LUO_SVG_16).setTargets(elementList).setTooltipText(COMMON_CONSTANT.BLANK_STRING);
            result.add(builder.createLineMarkerInfo(element));
        }
    }

    private List<PsiElement> getTargetArr(Project project, MappingAnnotation mappingAnnotation, String fileType) {
        List<PsiElement> elementList = new ArrayList<>();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        for (VirtualFile virtualFile : contentRoots) {
            if (virtualFile.getPath().contains("/src")) {
                PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
                if (null != psiDirectory) {
                    elementList.addAll(this.dealDirectory(psiDirectory, mappingAnnotation, fileType));
                }
            }
        }
        return elementList;
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
            if (file.getFileType() instanceof JavaFileType) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                PsiClass[] psiClassArr = psiJavaFile.getClasses();
                int psiClassCount = psiClassArr.length;
                //含有内部类跳过
                if (psiClassCount == 0 || psiClassCount > 1) {
                    break;
                }
                PsiClass psiClass = psiClassArr[0];
                PsiAnnotation[] psiAnnotationArr = psiClass.getAnnotations();
                // 无注解跳过
                if (psiAnnotationArr.length == 0) {
                    break;
                }
                if ((JavaFileUtil.isModuleController(psiClass, psiAnnotationArr) && COMMON_CONSTANT.FEIGN.equals(fileType)) || (JavaFileUtil.isFeign(psiAnnotationArr) && COMMON_CONSTANT.CONTROLLER.equals(fileType))) {
                    for (PsiMethod psiMethod : psiClass.getMethods()) {
                        MappingAnnotation targetMappingAnnotation = JavaFileUtil.getMappingAnnotation(psiMethod);
                        if (null != targetMappingAnnotation && mappingAnnotation.equals(targetMappingAnnotation)) {
                            methodList.add(psiMethod);
                        }
                    }
                }else {
                    break;
                }
            }else {
                break;
            }
        }
        return methodList;
    }
}
