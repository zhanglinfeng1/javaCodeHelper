package pers.zlf.plugin.marker;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
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
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/6 14:25
 */
public class MapperFastJumpProvider extends BaseLineMarkerProvider<PsiClass> {
    /** 类全名 */
    private String classFullName;
    /** 类中的方法map */
    private Map<String, PsiMethod> methodMap;

    @Override
    public boolean checkPsiElement(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            return !psiClass.isAnnotationType() && psiClass.isInterface() && psiClass.getMethods().length != 0;
        }
        return false;
    }

    @Override
    public void dealPsiElement() {
        //排除用注解实现的
        Predicate<PsiMethod> predicate = psiMethod -> Arrays.stream(psiMethod.getAnnotations()).noneMatch(a -> null != a.getQualifiedName() && Annotation.IBATIS_LIST.contains(a.getQualifiedName()));
        methodMap = Arrays.stream(element.getMethods()).filter(predicate).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
        if (methodMap.isEmpty()) {
            return;
        }
        // 注解方式跳转,跳转至对应方法
        jumpToMethod();
        // xml方式跳转,跳转至对应xml
        jumpToXml();
    }

    private void jumpToMethod() {
        Map<String, PsiClass[]> psiClassMap = new HashMap<>(16);
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(element.getProject());
        GlobalSearchScope searchScope = element.getResolveScope();
        Iterator<Map.Entry<String, PsiMethod>> iterator = methodMap.entrySet().iterator();
        while(iterator.hasNext()) {
            PsiMethod psiMethod = iterator.next().getValue();
            //存在 xxxxProvider注解
            Optional<PsiAnnotation> annotationOptional = Arrays.stream(psiMethod.getAnnotations()).filter(a -> null != a.getQualifiedName() && Annotation.IBATIS_PROVIDER_LIST.contains(a.getQualifiedName())).findAny();
            if (annotationOptional.isEmpty()) {
                continue;
            }
            //获取注解
            PsiAnnotation annotation = annotationOptional.get();
            //获取注解的type值
            String className = MyPsiUtil.getAnnotationValue(annotation, Annotation.TYPE).replace(ClassType.CLASS_FILE, Common.BLANK_STRING);
            //获取注解的method值
            String methodValue = MyPsiUtil.getAnnotationValue(annotation, Annotation.METHOD);
            //根据注解的type值查找PsiClass
            PsiClass[] psiClassArr = Optional.ofNullable(psiClassMap.get(className)).orElseGet(() -> {
                PsiClass[] searchResultArr = cache.getClassesByName(className, searchScope);
                psiClassMap.put(className, searchResultArr);
                return searchResultArr;
            });
            boolean notFind = true;
            //正常都会在一个类中，不用反复循环。防止不正常的人(っ °Д °;)っ
            loop:
            for (PsiClass psiClass : psiClassArr) {
                for (PsiMethod targetMethod : psiClass.getMethods()) {
                    if (methodValue.equals(targetMethod.getName())) {
                        addLineMarkerBoth(targetMethod, psiMethod);
                        notFind = false;
                        break loop;
                    }
                }
            }
            if (notFind) {
                //TODO 创建代码
            }
            iterator.remove();
        }
    }

    private void jumpToXml() {
        if (methodMap.isEmpty()) {
            return;
        }
        classFullName = element.getQualifiedName();
        Project project = element.getProject();
        PsiManager manager = PsiManager.getInstance(project);
        Optional.ofNullable(element.getContainingFile()).map(PsiFile::getVirtualFile).map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, project))
                .map(ModuleRootManager::getInstance).map(ModuleRootManager::getSourceRoots)
                .ifPresent(virtualFiles -> Arrays.stream(virtualFiles).filter(virtualFile -> virtualFile.getPath().endsWith(Common.RESOURCES))
                        .map(manager::findDirectory).filter(Objects::nonNull).forEach(this::findXml));
        //处理未找到跳转的方法
        methodMap.values().forEach(method -> {
            //TODO 创建代码
        });
    }

    private void findXml(PsiDirectory psiDirectory) {
        //文件夹
        Arrays.stream(psiDirectory.getSubdirectories()).forEach(this::findXml);
        //文件
        for (PsiFile psiFile : psiDirectory.getFiles()) {
            //处理xml文件
            if (psiFile instanceof XmlFile) {
                XmlFile xmlFile = (XmlFile) psiFile;
                XmlTag rootTag = XmlUtil.getRootTagByName(xmlFile, Xml.MAPPER);
                if (null == rootTag || !classFullName.equals(rootTag.getAttributeValue(Xml.NAMESPACE))) {
                    continue;
                }
                //寻找标签
                List<XmlTag> tagList = XmlUtil.findTags(rootTag, Xml.INSERT, Xml.UPDATE, Xml.DELETE, Xml.SELECT);
                for (XmlTag tag : tagList) {
                    Optional.ofNullable(tag.getAttributeValue(Xml.ID)).map(methodMap::get).ifPresent(method -> {
                        addLineMarker(method, tag);
                        methodMap.remove(method.getName());
                    });
                }
                break;
            }
        }
    }
}
