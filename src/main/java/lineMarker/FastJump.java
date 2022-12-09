package lineMarker;

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
import constant.REQUEST;
import pojo.MappingAnnotation;
import util.MyPsiUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:34
 */
public abstract class FastJump {
    /** 跳转类型 */
    public final String fastJumpType;
    /** 跳转目标 */
    public List<PsiMethod> methodList = new ArrayList<>();
    /** 注解解析对象 */
    private final MappingAnnotation mappingAnnotation;
    /** 过滤的文件名 */
    private final String filterFolderName;

    public FastJump(PsiClass psiClass, PsiMethod psiMethod, String filterFolderName, String fastJumpType) {
        this.filterFolderName = filterFolderName;
        this.fastJumpType = fastJumpType;
        //获取方法的注解
        mappingAnnotation = this.getMappingAnnotation(psiMethod.getAnnotations());
        if (null == mappingAnnotation) {
            return;
        }
        //获取类的注解路径
        String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
        mappingAnnotation.setUrl(classUrl + mappingAnnotation.getUrl());
        //当前项目路径
        Project project = psiMethod.getProject();
        String basePath = project.getBasePath();
        //当前模块路径
        String currentModulePath = COMMON.BLANK_STRING;
        if (null != basePath) {
            int index = basePath.lastIndexOf(COMMON.SLASH);
            if (-1 != index) {
                currentModulePath = psiClass.getContainingFile().getVirtualFile().getPath();
                currentModulePath = currentModulePath.substring(0, currentModulePath.indexOf(COMMON.SLASH, index + 1));
            }
        }
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            if ((StringUtil.isNotEmpty(currentModulePath) && virtualFile.getPath().contains(currentModulePath)) || virtualFile.getPath().contains("/resources")) {
                continue;
            }
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (null != psiDirectory) {
                dealDirectory(psiDirectory);
            }
        }
    }

    private void dealDirectory(PsiDirectory psiDirectory) {
        for (PsiDirectory subdirectory : psiDirectory.getSubdirectories()) {
            this.dealDirectory(subdirectory);
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
                MappingAnnotation targetMappingAnnotation = this.getMappingAnnotation(psiMethod.getAnnotations());
                if (null == targetMappingAnnotation) {
                    continue;
                }
                targetMappingAnnotation.setUrl(classUrl + targetMappingAnnotation.getUrl());
                if (mappingAnnotation.equals(targetMappingAnnotation)) {
                    methodList.add(psiMethod);
                }
            }
        }
    }

    public abstract boolean checkClass(PsiClass psiClass);

    public List<PsiMethod> getMethodList() {
        return methodList;
    }

    private MappingAnnotation getMappingAnnotation(PsiAnnotation[] psiAnnotationArr) {
        for (PsiAnnotation psiAnnotation : psiAnnotationArr) {
            String annotationName = psiAnnotation.getQualifiedName();
            if (null == annotationName) {
                continue;
            }
            //请求路径
            String methodUrl = getMappingUrl(psiAnnotation);
            if (StringUtil.isEmpty(methodUrl)) {
                continue;
            }
            //请求方式
            switch (annotationName) {
                case ANNOTATION.POST_MAPPING:
                    return new MappingAnnotation(methodUrl, REQUEST.POST);
                case ANNOTATION.PUT_MAPPING:
                    return new MappingAnnotation(methodUrl, REQUEST.PUT);
                case ANNOTATION.GET_MAPPING:
                    return new MappingAnnotation(methodUrl, REQUEST.GET);
                case ANNOTATION.DELETE_MAPPING:
                    return new MappingAnnotation(methodUrl, REQUEST.DELETE);
                case ANNOTATION.REQUEST_MAPPING:
                    String method = MyPsiUtil.getAnnotationValue(psiAnnotation, ANNOTATION.METHOD);
                    return new MappingAnnotation(methodUrl, REQUEST.METHOD_LIST.stream().filter(method::contains).findAny().orElse(method));
                default:
            }
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
