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
import constant.ANNOTATION_CONSTANT;
import constant.COMMON_CONSTANT;
import pojo.MappingAnnotation;
import util.PsiObjectUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:34
 */
public abstract class FastJump {
    public final String fastJumpType;
    public List<PsiMethod> methodList = new ArrayList<>();
    private final MappingAnnotation mappingAnnotation;
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
        String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
        mappingAnnotation.setUrl(classUrl + mappingAnnotation.getUrl());
        Project project = psiMethod.getProject();
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            if (!virtualFile.getPath().contains("/resources")) {
                PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
                if (null != psiDirectory) {
                    dealDirectory(psiDirectory);
                }
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
                break;
            }
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass psiClass = psiJavaFile.getClasses()[0];
            if (!checkClass(psiClass)) {
                continue;
            }
            //类注解路径
            String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
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
        PsiAnnotation annotation = null;
        for (PsiAnnotation psiAnnotation : psiAnnotationArr) {
            if (ANNOTATION_CONSTANT.MAPPING_LIST.contains(psiAnnotation.getQualifiedName())) {
                annotation = psiAnnotation;
                break;
            }
        }
        //方法注解
        String methodUrl = getMappingUrl(annotation);
        if (StringUtil.isEmpty(methodUrl)) {
            return null;
        }
        //请求方式
        String requestMethod = getMappingMethod(annotation);
        return new MappingAnnotation(methodUrl, requestMethod);
    }

    private String getMappingUrl(PsiAnnotation annotation) {
        if (null == annotation) {
            return COMMON_CONSTANT.BLANK_STRING;
        }
        String url = PsiObjectUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.VALUE);
        if (StringUtil.isEmpty(url)) {
            return PsiObjectUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.PATH);
        }
        return url;
    }

    private String getMappingMethod(PsiAnnotation annotation) {
        String method = ANNOTATION_CONSTANT.MAPPING_METHOD_MAP.get(Objects.requireNonNull(annotation.getQualifiedName()));
        if (StringUtil.isEmpty(method)) {
            method = PsiObjectUtil.getAnnotationValue(annotation, ANNOTATION_CONSTANT.METHOD);
            if (method.contains(COMMON_CONSTANT.DOT)) {
                method = method.substring(method.indexOf(COMMON_CONSTANT.DOT) + 1);
            }
        }
        return method;
    }
}
