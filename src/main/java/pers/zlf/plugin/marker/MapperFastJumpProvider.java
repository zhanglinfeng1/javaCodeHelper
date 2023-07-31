package pers.zlf.plugin.marker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.psi.PsiMethodModel;
import pers.zlf.plugin.pojo.psi.PsiParameterModel;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.XmlUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/6 14:25
 */
public class MapperFastJumpProvider extends BaseLineMarkerProvider<PsiClass> {
    /** sql语句类型 */
    private final Map<String, String> sqlTypeMap = new HashMap<>() {{
        put("add", "insert");
        put("insert", "insert");
        put("update", "update");
        put("edit", "update");
        put("delete", "delete");
    }};
    /** 类全名 */
    private String classFullName;
    /** 类中的方法map */
    private Map<String, PsiMethod> methodMap;
    /** 类所在项目 */
    private Project project;
    /** 模版名称 */
    private String templateName;

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
        project = element.getProject();
        templateName = Common.JUMP_TO_XML_TEMPLATE;
        // 注解方式跳转,跳转至对应方法
        jumpToMethod();
        // xml方式跳转,跳转至对应xml
        jumpToXml();
    }

    private void jumpToMethod() {
        Map<String, PsiClass[]> psiClassMap = new HashMap<>(4);
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        GlobalSearchScope searchScope = element.getResolveScope();
        for (PsiMethod psiMethod : element.getMethods()) {
            //存在 xxxxProvider注解
            Optional<PsiAnnotation> annotationOptional = Arrays.stream(psiMethod.getAnnotations()).filter(a -> null != a.getQualifiedName() && Annotation.IBATIS_PROVIDER_LIST.contains(a.getQualifiedName())).findAny();
            if (annotationOptional.isEmpty()) {
                continue;
            }
            templateName = Common.JUMP_TO_METHOD_TEMPLATE;
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
            if (psiClassArr.length == 0) {
                continue;
            }
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
            //生成代码
            if (notFind) {
                addMethodHandler(psiMethod, Empty.of(methodValue).orElse(psiMethod.getName()), psiClassArr[0]);
            }
        }
    }

    private void jumpToXml() {
        if (templateName.equals(Common.JUMP_TO_METHOD_TEMPLATE)) {
            return;
        }
        //排除用注解实现的
        Predicate<PsiMethod> predicate = psiMethod -> Arrays.stream(psiMethod.getAnnotations()).noneMatch(a -> null != a.getQualifiedName() && Annotation.IBATIS_LIST.contains(a.getQualifiedName()));
        methodMap = Arrays.stream(element.getMethods()).filter(predicate).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
        if (methodMap.isEmpty()) {
            return;
        }
        classFullName = element.getQualifiedName();
        PsiManager manager = PsiManager.getInstance(project);
        // mapper文件所在模块的资源文件
        Optional.ofNullable(element.getContainingFile())
                .map(PsiFile::getVirtualFile)
                .map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, project))
                .map(ModuleRootManager::getInstance)
                .map(ModuleRootManager::getSourceRoots)
                .ifPresent(virtualFiles -> {
                    //处理resources目录下的文件
                    for (VirtualFile virtualFile : virtualFiles) {
                        if (virtualFile.getPath().endsWith(Common.RESOURCES)) {
                            // 跳转目标所在的xml文件
                            Optional.ofNullable(manager.findDirectory(virtualFile)).ifPresent(this::findXml);
                        }
                    }
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
                XmlTag mapperTag = XmlUtil.getRootTagByName(xmlFile, Xml.MAPPER);
                if (null == mapperTag || !classFullName.equals(mapperTag.getAttributeValue(Xml.NAMESPACE))) {
                    continue;
                }
                //寻找标签
                List<XmlTag> tagList = XmlUtil.findTags(mapperTag, Xml.INSERT, Xml.UPDATE, Xml.DELETE, Xml.SELECT);
                for (XmlTag tag : tagList) {
                    Optional.ofNullable(tag.getAttributeValue(Xml.ID)).map(methodMap::get).ifPresent(method -> {
                        addLineMarker(method, tag);
                        methodMap.remove(method.getName());
                    });
                }
                //处理未找到跳转的方法
                methodMap.values().forEach(method -> addXmlHandler(method, mapperTag));
                return;
            }
        }
    }

    /**
     * 创建方法代码
     *
     * @param psiMethod  待补全的方法
     * @param methodName 需要补全的方法名
     * @param psiClass   目标文件
     */
    private void addMethodHandler(PsiMethod psiMethod, String methodName, PsiClass psiClass) {
        Function<String, PsiMethod> function = code -> JavaPsiFacade.getInstance(project).getElementFactory().createMethodFromText(code, psiClass);
        addHandler(psiMethod, methodName, function, psiClass::add, psiClass);
    }

    /**
     * 创建xml代码
     *
     * @param psiMethod 待补全的方法
     * @param mapperTag mapper标签
     */
    private void addXmlHandler(PsiMethod psiMethod, XmlTag mapperTag) {
        Function<String, XmlTag> function = code -> XmlElementFactory.getInstance(project).createTagFromText(code);
        addHandler(psiMethod, psiMethod.getName(), function, element -> mapperTag.addSubTag(element, false), mapperTag);
    }

    /**
     * 创建方法代码
     *
     * @param psiMethod     待补全的方法
     * @param methodName    需要补全的方法名
     * @param function      添加元素
     * @param consumer      添加动作
     * @param targetElement 目标元素
     */
    private <T extends PsiElement> void addHandler(PsiMethod psiMethod, String methodName, Function<String, T> function, Consumer<T> consumer, PsiElement targetElement) {
        //模版数据
        PsiMethodModel methodModel = new PsiMethodModel(methodName, psiMethod.getReturnType());
        List<PsiParameterModel> modelList = Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameterModel::new).collect(Collectors.toList());
        methodModel.setParameterModelList(modelList);
        Optional<String> sqlType = sqlTypeMap.entrySet().stream().filter(t -> methodName.startsWith(t.getKey())).map(Map.Entry::getValue).findAny();
        methodModel.setSqlType(sqlType.orElse(Common.SELECT));
        //生成代码
        String code = TemplateFactory.getInstance().getTemplateContent(templateName, JsonUtil.toMap(methodModel));
        T newElement = function.apply(code);
        //TODO 低版本不兼容GutterIconNavigationHandler
        GutterIconNavigationHandler<PsiElement> handler = (e, elt) -> ApplicationManager.getApplication().runWriteAction(() -> {
            consumer.accept(newElement);
            CodeStyleManager.getInstance(project).reformat(newElement);
            MyPsiUtil.moveToPsiElement(targetElement, -code.length());
        });
        addHandler(psiMethod, newElement, handler);
    }

}
