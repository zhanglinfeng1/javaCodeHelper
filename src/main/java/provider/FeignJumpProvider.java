package provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
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
public class FeignJumpProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiMethod) {
            String fileType;
            if (JavaFileUtil.isFeign(element)) {
                fileType = COMMON_CONSTANT.FEIGN;
            } else if (JavaFileUtil.isModuleController(element)) {
                fileType = COMMON_CONSTANT.CONTROLLER;
            } else {
                return;
            }
            //获取注解路径
            MappingAnnotation mappingAnnotation = JavaFileUtil.getMappingAnnotation(element);
            if (null == mappingAnnotation) {
                return;
            }
            //寻找文件
            List<PsiElement> elementList = this.getTargetArr(element.getProject(), mappingAnnotation, fileType);
            if (elementList.isEmpty()) {
                return;
            }
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(ICON_CONSTANT.BO_LUO_SVG_16).setTargets(elementList).setTooltipText("Jump to module");
            result.add(builder.createLineMarkerInfo(element));
        }
    }

    private List<PsiElement> getTargetArr(Project project, MappingAnnotation mappingAnnotation, String fileType) {
        List<PsiElement> elementList = new ArrayList<>();

        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        for (VirtualFile virtualFile : contentRoots) {
            if (virtualFile.getPath().contains("/src")) {
                PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
                if (null == psiDirectory) {
                    continue;
                }
                PsiElement psiElement = this.dealDirectory(psiDirectory, mappingAnnotation, fileType);
                if (null != psiElement) {
                    elementList.add(psiElement);
                }
            }
        }
        return elementList;
    }

    private PsiMethod dealDirectory(PsiDirectory psiDirectory, MappingAnnotation mappingAnnotation, String fileType) {
        PsiDirectory[] subdirectories = psiDirectory.getSubdirectories();
        for (PsiDirectory subdirectory : subdirectories) {
            PsiMethod psiMethod = this.dealDirectory(subdirectory, mappingAnnotation, fileType);
            if (null != psiMethod) {
                return psiMethod;
            }
        }
        PsiFile[] files = psiDirectory.getFiles();
        for (PsiFile file : files) {
            // 不是 Java 类型的文件直接跳过
            if (file.getFileType() instanceof JavaFileType) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                PsiClass psiClass = psiJavaFile.getClasses()[0];
                if ((JavaFileUtil.isModuleController(psiClass) && COMMON_CONSTANT.FEIGN.equals(fileType)) || (JavaFileUtil.isFeign(psiClass) && COMMON_CONSTANT.CONTROLLER.equals(fileType))) {
                    for (PsiMethod psiMethod : psiClass.getMethods()) {
                        MappingAnnotation targetMappingAnnotation = JavaFileUtil.getMappingAnnotation(psiMethod);
                        if (null != targetMappingAnnotation && mappingAnnotation.equals(targetMappingAnnotation)) {
                            return psiMethod;
                        }
                    }
                }
            }
        }
        return null;
    }
}
