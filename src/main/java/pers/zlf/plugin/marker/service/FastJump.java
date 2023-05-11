package pers.zlf.plugin.marker.service;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
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
import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON;
import pers.zlf.plugin.constant.COMMON_ENUM;
import pers.zlf.plugin.constant.COMMON_ENUM_TYPE;
import pers.zlf.plugin.constant.REQUEST;
import pers.zlf.plugin.pojo.MappingAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:34
 */
public abstract class FastJump {
    /** 处理结果 */
    public Map<String, MappingAnnotation> map;
    /** 过滤的文件名 */
    private final String filterFolderName;

    public FastJump(String filterFolderName) {
        this.filterFolderName = filterFolderName;
    }

    public void addLineMarker(Collection<? super RelatedItemLineMarkerInfo<?>> result, PsiClass psiClass) {
        //获取类的注解路径
        String classUrl = this.getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
        //获取方法的注解
        map = Arrays.stream(psiClass.getMethods()).map(method -> this.getMappingAnnotation(classUrl, method)).filter(Objects::nonNull).collect(Collectors.toMap(MappingAnnotation::toString, Function.identity(), (k1, k2) -> k2));
        if (map.isEmpty()) {
            return;
        }
        //当前模块路径
        Project project = psiClass.getProject();
        String currentModulePath = MyPsiUtil.getCurrentModulePath(psiClass.getContainingFile().getVirtualFile(), project);
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            String virtualFilePath = virtualFile.getPath();
            if ((StringUtil.isNotEmpty(currentModulePath) && virtualFilePath.startsWith(currentModulePath)) || virtualFilePath.endsWith(COMMON.RESOURCES)) {
                continue;
            }
            if (jump(virtualFilePath)) {
                Optional.ofNullable(PsiManager.getInstance(project).findDirectory(virtualFile)).ifPresent(this::dealDirectory);
            }
        }
        map.values().stream().filter(t -> !t.getTargetList().isEmpty()).forEach(t -> result.add(NavigationGutterIconBuilder.create(ICON.LOGO)
                .setTargets(t.getTargetList()).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(t.getPsiAnnotation())));
    }

    public String getMappingUrl(PsiAnnotation annotation) {
        return Optional.ofNullable(annotation).map(psiAnnotation -> {
            String url = MyPsiUtil.getAnnotationValue(psiAnnotation, ANNOTATION.VALUE);
            return StringUtil.isEmpty(url) ? MyPsiUtil.getAnnotationValue(psiAnnotation, ANNOTATION.PATH) : url;
        }).orElse(COMMON.BLANK_STRING);
    }

    public abstract boolean jump(String virtualFilePath);

    public abstract boolean checkClass(PsiClass psiClass);

    public abstract String getClassUrl(PsiClass psiClass);

    private void dealDirectory(PsiDirectory psiDirectory) {
        //处理文件夹
        Arrays.stream(psiDirectory.getSubdirectories()).forEach(this::dealDirectory);
        //只处理符合的文件夹名下的文件
        if (StringUtil.isNotEmpty(filterFolderName) && !psiDirectory.getName().contains(filterFolderName)) {
            return;
        }
        Arrays.stream(psiDirectory.getFiles()).filter(f -> f.getFileType() instanceof JavaFileType).map(f -> ((PsiJavaFile) f).getClasses())
                .filter(classes -> classes.length == 1).map(classes -> classes[0]).filter(this::checkClass)
                .forEach(psiClass -> {
                    String classUrl = this.getClassUrl(psiClass);
                    Arrays.stream(psiClass.getMethods()).forEach(method -> {
                        MappingAnnotation mappingAnnotation = this.getMappingAnnotation(classUrl, method);
                        if (null != mappingAnnotation){
                            MappingAnnotation t = map.get(mappingAnnotation.toString());
                            if (null != t){
                                t.getTargetList().add(method);
                            }
                        }

                    });
                });
    }

    private MappingAnnotation getMappingAnnotation(String classUrl, PsiMethod psiMethod) {
        for (PsiAnnotation psiAnnotation : psiMethod.getAnnotations()) {
            String annotationName = psiAnnotation.getQualifiedName();
            if (null == annotationName || !ANNOTATION.MAPPING_LIST.contains(annotationName)) {
                continue;
            }
            //请求方式
            String method = Empty.of(COMMON_ENUM.select(COMMON_ENUM_TYPE.REQUEST_TYPE, annotationName)).map(COMMON_ENUM::getValue).orElse(MyPsiUtil.getAnnotationValue(psiAnnotation, ANNOTATION.METHOD));
            Optional<String> optional = REQUEST.TYPE_LIST.stream().filter(method::contains).findAny();
            if (optional.isPresent()){
                method = optional.get();
            }
            if (StringUtil.isNotEmpty(method)) {
                //请求路径
                String methodUrl = getMappingUrl(psiAnnotation);
                return StringUtil.isNotEmpty(methodUrl) ? new MappingAnnotation(psiAnnotation, classUrl + COMMON.SLASH + methodUrl, method) : null;
            }
        }
        return null;
    }

}
