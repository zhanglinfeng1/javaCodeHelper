package lineMarker;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import constant.ANNOTATION_CONSTANT;
import pojo.MappingAnnotation;
import util.JavaFileUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:34
 */
public class FastJump {
    public final String fastJumpType;
    public List<PsiMethod> methodList = new ArrayList<>();
    private final MappingAnnotation mappingAnnotation;
    private final String filterFolderName;
    private final String psiMethodReturnType;

    public FastJump(PsiClass psiClass, PsiMethod psiMethod, String filterFolderName,String fastJumpType) {
        this.filterFolderName = filterFolderName;
        this.fastJumpType = fastJumpType;
        this.psiMethodReturnType = psiMethod.getReturnType().getPresentableText();
        //获取方法的注解
        mappingAnnotation = JavaFileUtil.getMappingAnnotation(psiMethod.getAnnotations());
        if (null == mappingAnnotation) {
            return;
        }
        //获取类的注解路径
        String classUrl = JavaFileUtil.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
        mappingAnnotation.setUrl(classUrl + mappingAnnotation.getUrl());
        if (StringUtil.isEmpty(mappingAnnotation.getUrl())) {
            return;
        }
        Project project = psiMethod.getProject();
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentRoots()) {
            if (virtualFile.getPath().contains("/src")) {
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
                continue;
            }
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass[] psiClassArr = psiJavaFile.getClasses();
            if (psiClassArr.length == 0) {
                continue;
            }
            PsiClass psiClass = psiClassArr[0];
            if (!checkClass(psiClass)) {
                continue;
            }
            //类注解路径
            String classUrl = JavaFileUtil.getMappingUrl(psiClass.getAnnotation(ANNOTATION_CONSTANT.REQUEST_MAPPING));
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                //返回类型不一致
                if(!psiMethodReturnType.equals(psiMethod.getReturnType().getPresentableText())){
                    continue;
                }
                //获取方法的注解
                MappingAnnotation targetMappingAnnotation = JavaFileUtil.getMappingAnnotation(psiMethod.getAnnotations());
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

    public boolean checkClass(PsiClass psiClass) {
        return true;
    }

    public List<PsiMethod> getMethodList() {
        return methodList;
    }

}
