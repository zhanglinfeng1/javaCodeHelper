package lineMarker;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import constant.ANNOTATION;
import constant.COMMON;
import constant.TYPE;
import constant.XML;
import util.MyPsiUtil;
import util.StringUtil;
import util.XmlUtil;

import java.util.Arrays;
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
    private String classFullName;
    private Map<String, PsiMethod> methodMap;

    @Override
    public boolean checkPsiElement(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            return psiClass.isInterface() && psiClass.getMethods().length != 0;
        }
        return false;
    }

    @Override
    public void dealPsiElement() {
        // 注解方式跳转
        addByAnnotation();
        // xml方式跳转
        addByXml();
    }

    private void addByAnnotation() {
        Map<String, PsiClass[]> psiClassMap = new HashMap<>();
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(element.getProject());
        GlobalSearchScope searchScope = element.getResolveScope();
        for (PsiMethod psiMethod : element.getMethods()) {
            Optional<PsiAnnotation> annotationOptional = Arrays.stream(psiMethod.getAnnotations()).filter(a -> null != a.getQualifiedName() && ANNOTATION.IBATIS_PROVIDER_LIST.contains(a.getQualifiedName())).findAny();
            if (annotationOptional.isPresent()) {
                PsiAnnotation annotation = annotationOptional.get();
                String className = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.TYPE).replace(TYPE.CLASS_FILE_SUFFIX, COMMON.BLANK_STRING);
                PsiClass[] psiClassArr = Optional.ofNullable(psiClassMap.get(className)).orElseGet(() -> {
                    PsiClass[] searchResultArr = cache.getClassesByName(className, searchScope);
                    psiClassMap.put(className, searchResultArr);
                    return searchResultArr;
                });
                Arrays.stream(psiClassArr).map(c -> Arrays.stream(c.getMethods()).filter(m -> MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.METHOD).equals(m.getName())).findAny())
                        .filter(Optional::isPresent).map(Optional::get).findAny().ifPresent(t -> addLineMarkerBoth(t, psiMethod));
            }
        }
    }

    private void addByXml() {
        String currentModulePath = MyPsiUtil.getCurrentModulePath(element);
        if (StringUtil.isNotEmpty(currentModulePath)) {
            Project project = element.getProject();
            classFullName = element.getQualifiedName();
            methodMap = Arrays.stream(element.getMethods()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
            for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                if (virtualFile.getPath().contains(currentModulePath)) {
                    Optional.ofNullable(PsiManager.getInstance(project).findDirectory(virtualFile)).ifPresent(this::findXml);
                }
            }
        }
    }

    private void findXml(PsiDirectory psiDirectory) {
        for (PsiDirectory subdirectory : psiDirectory.getSubdirectories()) {
            this.findXml(subdirectory);
        }
        Arrays.stream(psiDirectory.getFiles()).filter(f -> f instanceof XmlFile).forEach(f -> {
            XmlTag rootTag = XmlUtil.getRootTagByName((XmlFile) f, XML.MAPPER);
            if (null != rootTag && classFullName.equals(rootTag.getAttributeValue(XML.NAMESPACE))) {
                XmlUtil.findTags(rootTag, XML.INSERT, XML.UPDATE, XML.DELETE, XML.SELECT).forEach(x -> Optional.ofNullable(methodMap.get(x.getAttributeValue(XML.ID))).ifPresent(m -> addLineMarker(x, m)));
            }
        });
    }
}
