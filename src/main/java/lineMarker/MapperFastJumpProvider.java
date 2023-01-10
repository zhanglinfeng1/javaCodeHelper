package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import constant.ANNOTATION;
import constant.COMMON;
import constant.ICON;
import constant.TYPE;
import constant.XML;
import org.jetbrains.annotations.NotNull;
import util.MyPsiUtil;
import util.StringUtil;
import util.XmlUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/6 14:25
 */
public class MapperFastJumpProvider extends AbstractLineMarkerProvider<PsiClass> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            return psiClass.isInterface() && psiClass.getMethods().length != 0;
        }
        return false;
    }

    @Override
    public void addLineMarker(PsiClass psiClass, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        // 注解方式跳转
        addByAnnotation(psiClass, result);
        // xml方式跳转
        addByXml(psiClass, result);
    }

    private void addByAnnotation(PsiClass psiClass, Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Map<String, PsiClass[]> psiClassMap = new HashMap<>();
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(psiClass.getProject());
        GlobalSearchScope searchScope = psiClass.getResolveScope();
        loop:
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            Optional<PsiAnnotation> annotationOptional = Arrays.stream(psiMethod.getAnnotations()).filter(a -> null != a.getQualifiedName() && ANNOTATION.IBATIS_PROVIDER_LIST.contains(a.getQualifiedName())).findAny();
            if (annotationOptional.isPresent()) {
                PsiAnnotation annotation = annotationOptional.get();
                String className = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.TYPE).replace(TYPE.CLASS_FILE_SUFFIX, COMMON.BLANK_STRING);
                PsiClass[] psiClassArr = Optional.ofNullable(psiClassMap.get(className)).orElseGet(() -> {
                    PsiClass[] searchResultArr = cache.getClassesByName(className, searchScope);
                    psiClassMap.put(className, searchResultArr);
                    return searchResultArr;
                });
                String method = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.METHOD);
                for (PsiClass targetClass : psiClassArr) {
                    for (PsiMethod targetMethod : targetClass.getMethods()) {
                        if (method.equals(targetMethod.getName())) {
                            result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(targetMethod).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(psiMethod));
                            result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(psiMethod).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(targetMethod));
                            continue loop;
                        }
                    }
                }
            }
        }
    }

    private void addByXml(PsiClass psiClass, Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        String currentModulePath = MyPsiUtil.getCurrentModulePath(psiClass);
        if (StringUtil.isNotEmpty(currentModulePath)) {
            Project project = psiClass.getProject();
            String classFullName = psiClass.getQualifiedName();
            Map<String, PsiMethod> methodMap = Arrays.stream(psiClass.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
            for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                if (virtualFile.getPath().contains(currentModulePath)) {
                    Optional.ofNullable(PsiManager.getInstance(project).findDirectory(virtualFile)).ifPresent(t -> findXml(classFullName, methodMap, t, result));
                }
            }
        }
    }

    private void findXml(String classFullName, Map<String, PsiMethod> methodMap, PsiDirectory psiDirectory, Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        for (PsiDirectory subdirectory : psiDirectory.getSubdirectories()) {
            this.findXml(classFullName, methodMap, subdirectory, result);
        }
        PsiFile[] files = psiDirectory.getFiles();
        for (PsiFile file : files) {
            if (!(file instanceof XmlFile)) {
                continue;
            }
            XmlFile xmlFile = (XmlFile) file;
            XmlTag rootTag = XmlUtil.getRootTagByName(xmlFile, XML.MAPPER);
            if (null == rootTag || !classFullName.equals(rootTag.getAttributeValue(XML.NAMESPACE))) {
                continue;
            }
            XmlUtil.findTags(rootTag, XML.INSERT, XML.UPDATE, XML.DELETE, XML.SELECT).forEach(x -> Optional.ofNullable(methodMap.get(x.getAttributeValue(XML.ID))).ifPresent(m -> result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(x).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(m))));
        }
    }
}
