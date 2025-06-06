package pers.zlf.plugin.marker.feign;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Request;
import pers.zlf.plugin.pojo.MappingAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.PathUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/17 16:34
 */
public abstract class BaseFastJump {
    /** 处理结果 */
    private Map<String, MappingAnnotation> map;
    /** 过滤的文件名 */
    private final String FILTER_FOLDER_NAME;
    /** 请求方式 */
    private final Map<String, String> REQUEST_TYPE_MAP = new HashMap<>() {{
        put(Annotation.POST_MAPPING, Request.POST);
        put(Annotation.PUT_MAPPING, Request.PUT);
        put(Annotation.GET_MAPPING, Request.GET);
        put(Annotation.DELETE_MAPPING, Request.DELETE);
        put(Annotation.PATCH_MAPPING, Request.PATCH);
    }};

    public BaseFastJump(String filterFolderName) {
        this.FILTER_FOLDER_NAME = filterFolderName;
    }

    public Map<String, MappingAnnotation> getLineMarkerMap(PsiClass psiClass) {
        //获取类的注解路径
        String classUrl = this.getMappingUrl(psiClass.getAnnotation(Annotation.REQUEST_MAPPING));
        //获取方法的注解
        map = Arrays.stream(psiClass.getMethods()).map(method -> this.getMappingAnnotation(classUrl, method)).filter(Objects::nonNull).collect(Collectors.toMap(MappingAnnotation::toString, Function.identity(), (k1, k2) -> k2));
        if (!map.isEmpty()) {
            //当前模块路径
            Project project = psiClass.getProject();
            String currentModulePath = MyPsiUtil.getCurrentModulePath(psiClass.getContainingFile().getVirtualFile(), project).toString();
            for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                //排除当前模块，排除资源文件路径
                String virtualFilePath = PathUtil.format(virtualFile.getPath());
                boolean currentModule = (StringUtil.isNotEmpty(currentModulePath) && virtualFilePath.startsWith(currentModulePath));
                if (currentModule || !virtualFilePath.endsWith(Common.JAVA)) {
                    continue;
                }
                //controller跳转feign 固定true
                //feign跳转controller 需排除配置的模块
                if (jump(project, virtualFile)) {
                    Optional.ofNullable(PsiManager.getInstance(project).findDirectory(virtualFile)).ifPresent(this::dealDirectory);
                }
            }
        }
        return map;
    }

    protected String getMappingUrl(PsiAnnotation annotation) {
        if (null == annotation) {
            return Common.BLANK_STRING;
        }
        String url = MyPsiUtil.getAnnotationValue(annotation, Annotation.VALUE);
        if (StringUtil.isNotEmpty(url)) {
            return url;
        }
        return MyPsiUtil.getAnnotationValue(annotation, Annotation.PATH);
    }

    /**
     * 是否需要添加跳转
     *
     * @param project     文件所在项目
     * @param virtualFile VirtualFile文件
     * @return boolean
     */
    protected abstract boolean jump(Project project, VirtualFile virtualFile);

    /**
     * 校验类是否符合跳转要求
     *
     * @param psiClass psiClass
     * @return boolean
     */
    protected abstract boolean checkTargetClass(PsiClass psiClass);

    /**
     * 获取类注解上的请求路径
     *
     * @param psiClass psiClass
     * @return String
     */
    protected abstract String getClassUrl(PsiClass psiClass);

    private void dealDirectory(PsiDirectory psiDirectory) {
        //处理文件夹
        Arrays.stream(psiDirectory.getSubdirectories()).forEach(this::dealDirectory);
        //只处理符合的文件夹名下的文件
        if (StringUtil.isNotEmpty(FILTER_FOLDER_NAME) && !psiDirectory.getName().contains(FILTER_FOLDER_NAME)) {
            return;
        }
        //筛选不存在内部类的java文件
        List<PsiClass> psiClassList = Arrays.stream(psiDirectory.getFiles())
                .filter(f -> f.getFileType() instanceof JavaFileType)
                .map(f -> ((PsiJavaFile) f).getClasses())
                .filter(classes -> classes.length == 1)
                .map(classes -> classes[0])
                .filter(this::checkTargetClass).toList();
        //处理类中的方法
        for (PsiClass psiClass : psiClassList) {
            String classUrl = this.getClassUrl(psiClass);
            for (PsiMethod method : psiClass.getMethods()) {
                MappingAnnotation mappingAnnotation = this.getMappingAnnotation(classUrl, method);
                if (null == mappingAnnotation) {
                    continue;
                }
                Optional.ofNullable(map.get(mappingAnnotation.toString())).ifPresent(t -> t.targetList().add(method));
            }
        }
    }

    private MappingAnnotation getMappingAnnotation(String classUrl, PsiMethod psiMethod) {
        //获取注解
        List<String> mappingList = List.of(Annotation.REQUEST_MAPPING, Annotation.POST_MAPPING, Annotation.GET_MAPPING, Annotation.PUT_MAPPING, Annotation.DELETE_MAPPING, Annotation.PATCH_MAPPING);
        PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(psiMethod.getAnnotations(), mappingList);
        if (null == psiAnnotation) {
            return null;
        }
        String annotationName = psiAnnotation.getQualifiedName();
        //请求方式
        String method = Empty.of(REQUEST_TYPE_MAP.get(annotationName)).orElse(Common.BLANK_STRING);
        if (StringUtil.isEmpty(method)) {
            method = MyPsiUtil.getAnnotationValue(psiAnnotation, Annotation.METHOD).toUpperCase();
        }
        method = Request.TYPE_LIST.stream().filter(method::contains).findAny().orElse(method);
        if (StringUtil.isNotEmpty(method)) {
            //请求路径
            String methodUrl = getMappingUrl(psiAnnotation);
            return StringUtil.isNotEmpty(methodUrl) ? new MappingAnnotation(classUrl + Common.SLASH + methodUrl, method, psiAnnotation, new ArrayList<>()) : null;
        }
        return null;
    }

}
