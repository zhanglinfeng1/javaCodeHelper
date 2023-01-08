package service;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import constant.ANNOTATION;
import constant.COMMON;
import constant.ICON;
import constant.REQUEST;
import pojo.MappingAnnotation;
import util.MyPsiUtil;
import util.StringUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:34
 */
public abstract class FastJump {
    /** 跳转类型 */
    public String fastJumpType;
    /** 过滤的文件名 */
    private final String filterFolderName;

    public FastJump(String filterFolderName) {
        this.filterFolderName = filterFolderName;
    }

    public void addLineMarker(Collection<? super RelatedItemLineMarkerInfo<?>> result, PsiClass psiClass, String fastJumpType) {
        this.fastJumpType = fastJumpType;
        //获取类的注解路径
        String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
        //当前模块路径
        String currentModulePath = MyPsiUtil.getCurrentModulePath(psiClass);
        Map<String, MappingAnnotation> map = new HashMap<>();
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            //获取方法的注解
            MappingAnnotation mappingAnnotation = this.getMappingAnnotation(classUrl, psiMethod);
            if (null == mappingAnnotation) {
                continue;
            }
            map.put(mappingAnnotation.toString(), mappingAnnotation);
        }
        Project project = psiClass.getProject();
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            if ((StringUtil.isNotEmpty(currentModulePath) && virtualFile.getPath().contains(currentModulePath)) || virtualFile.getPath().contains("/resources")) {
                continue;
            }
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (null != psiDirectory) {
                dealDirectory(map, psiDirectory);
                if (end(map)) {
                    break;
                }
            }
        }
        for (MappingAnnotation mappingAnnotation : map.values()) {
            if (!mappingAnnotation.getTargetMethodList().isEmpty()) {
                result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(mappingAnnotation.getTargetMethodList()).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(mappingAnnotation.getPsiMethod()));
            }
        }
    }

    public abstract boolean end(Map<String, MappingAnnotation> map);

    public abstract boolean checkClass(PsiClass psiClass);

    private void dealDirectory(Map<String, MappingAnnotation> map, PsiDirectory psiDirectory) {
        for (PsiDirectory subdirectory : psiDirectory.getSubdirectories()) {
            this.dealDirectory(map, subdirectory);
        }
        //只处理符合的文件夹名下的文件
        if (StringUtil.isNotEmpty(filterFolderName) && !psiDirectory.getName().contains(filterFolderName)) {
            return;
        }
        PsiFile[] files = psiDirectory.getFiles();
        for (PsiFile file : files) {
            // 不是 Java 类型的文件直接跳过
            if (!(file.getFileType() instanceof JavaFileType)) {
                continue;
            }
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass psiClass = psiJavaFile.getClasses()[0];
            if (!checkClass(psiClass)) {
                continue;
            }
            //类注解路径
            String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                //获取方法的注解
                MappingAnnotation targetMappingAnnotation = this.getMappingAnnotation(classUrl, psiMethod);
                if (null == targetMappingAnnotation) {
                    continue;
                }
                MappingAnnotation mappingAnnotation = map.get(targetMappingAnnotation.toString());
                if (null != mappingAnnotation) {
                    mappingAnnotation.getTargetMethodList().add(psiMethod);
                }
            }
        }
    }

    private MappingAnnotation getMappingAnnotation(String classUrl, PsiMethod psiMethod) {
        for (PsiAnnotation psiAnnotation : psiMethod.getAnnotations()) {
            String annotationName = psiAnnotation.getQualifiedName();
            if (null == annotationName) {
                continue;
            }
            //请求方式
            String method;
            switch (annotationName) {
                case ANNOTATION.POST_MAPPING:
                    method = REQUEST.POST;
                    break;
                case ANNOTATION.PUT_MAPPING:
                    method = REQUEST.PUT;
                    break;
                case ANNOTATION.GET_MAPPING:
                    method = REQUEST.GET;
                    break;
                case ANNOTATION.DELETE_MAPPING:
                    method = REQUEST.DELETE;
                    break;
                case ANNOTATION.REQUEST_MAPPING:
                    method = MyPsiUtil.getAnnotationValue(psiAnnotation, ANNOTATION.METHOD);
                    break;
                default:
                    continue;
            }
            //请求路径
            String methodUrl = getMappingUrl(psiAnnotation);
            if (StringUtil.isNotEmpty(methodUrl)) {
                return new MappingAnnotation(psiMethod, classUrl + COMMON.SLASH + methodUrl, method);
            }
            return null;
        }
        return null;
    }

    private String getMappingUrl(PsiAnnotation annotation) {
        if (null == annotation) {
            return COMMON.BLANK_STRING;
        }
        String url = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.VALUE);
        if (StringUtil.isEmpty(url)) {
            return MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.PATH);
        }
        return url;
    }

}
