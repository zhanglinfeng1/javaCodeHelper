package pers.zlf.plugin.marker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
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
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import pers.zlf.plugin.constant.Annotation;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/6 14:25
 */
public class MapperLineMarkerProvider extends BaseLineMarkerProvider<PsiClass> {
    /** sql语句类型 */
    private final Map<String, String> SQL_TYPE_MAP = new HashMap<>() {{
        put("add", "insert");
        put("insert", "insert");
        put("update", "update");
        put("edit", "update");
        put("delete", "delete");
        put("remove", "delete");
    }};
    private final String JUMP_TO_METHOD_TEMPLATE = "JumpToMethod.ftl";
    private final String JUMP_TO_XML_TEMPLATE = "JumpToXml.ftl";

    /** 类全名 */
    private String classFullName;
    /** 类中的方法map */
    private Map<String, PsiMethod> methodMap;
    /** 类所在项目 */
    private Project project;
    /** 找到xml文件 */
    private boolean findXml;
    /** 模版名称 */
    private String templateName;

    @Override
    protected boolean checkPsiElement(PsiElement element) {
        if (element instanceof PsiClass psiClass) {
            return !psiClass.isAnnotationType() && psiClass.isInterface() && psiClass.getMethods().length != 0;
        }
        return false;
    }

    @Override
    protected void dealPsiElement() {
        project = currentElement.getProject();
        templateName = null;
        // 注解方式跳转,跳转至对应方法
        jumpToMethod();
        // xml方式跳转,跳转至对应xml
        jumpToXml();
    }

    /**
     * 只从当前类的内部类查找跳转方法
     */
    private void jumpToMethod() {
        PsiClass targetClass = Optional.of(currentElement.getAllInnerClasses()).filter(t -> t.length > 0).map(t -> t[0]).orElse(null);
        if (null == targetClass) {
            return;
        }
        for (PsiMethod psiMethod : currentElement.getMethods()) {
            //存在 xxxxProvider注解
            List<String> providerList = List.of(Annotation.IBATIS_SELECT_PROVIDER, Annotation.IBATIS_INSERT_PROVIDER, Annotation.IBATIS_UPDATE_PROVIDER, Annotation.IBATIS_DELETE_PROVIDER);
            PsiAnnotation annotation = MyPsiUtil.findAnnotation(psiMethod.getAnnotations(), providerList);
            if (null == annotation) {
                continue;
            }
            //获取注解的method值
            String methodValue = MyPsiUtil.getAnnotationValue(annotation, Annotation.METHOD);
            Optional<PsiMethod> targetMethod = Arrays.stream(targetClass.getMethods()).filter(method -> methodValue.equals(method.getName())).findAny();
            if (targetMethod.isPresent()) {
                //添加跳转
                addLineMarkerBoth(targetMethod.get().getNameIdentifier(), annotation);
            } else {
                //生成代码
                templateName = JUMP_TO_METHOD_TEMPLATE;
                Function<String, PsiMethod> function = code -> {
                    PsiMethod method = JavaPsiFacade.getInstance(project).getElementFactory().createMethodFromText(code, targetClass);
                    targetClass.add(method);
                    return method;
                };
                addHandler(psiMethod, annotation, function, annotation, targetClass);
            }
        }
    }

    private void jumpToXml() {
        if (JUMP_TO_METHOD_TEMPLATE.equals(templateName)) {
            return;
        }
        templateName = JUMP_TO_XML_TEMPLATE;
        //排除用注解实现的
        List<String> ibatisList = List.of(Annotation.IBATIS_SELECT, Annotation.IBATIS_INSERT, Annotation.IBATIS_UPDATE, Annotation.IBATIS_DELETE,
                Annotation.IBATIS_SELECT_PROVIDER, Annotation.IBATIS_INSERT_PROVIDER, Annotation.IBATIS_UPDATE_PROVIDER, Annotation.IBATIS_DELETE_PROVIDER);
        Predicate<PsiMethod> predicate = psiMethod -> null == MyPsiUtil.findAnnotation(psiMethod.getAnnotations(), ibatisList);
        methodMap = Arrays.stream(currentElement.getMethods()).filter(predicate).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (k1, k2) -> k2));
        if (methodMap.isEmpty()) {
            return;
        }
        findXml = false;
        classFullName = currentElement.getQualifiedName();
        PsiManager manager = PsiManager.getInstance(project);
        // 查询跳转目标所在的xml文件
        for (VirtualFile virtualFile : MyPsiUtil.getModuleSourceRoots(currentElement)) {
            if (virtualFile.getPath().endsWith(Common.RESOURCES)) {
                Optional.ofNullable(manager.findDirectory(virtualFile)).ifPresent(this::findXml);
            }
        }
    }

    private void findXml(PsiDirectory psiDirectory) {
        if (findXml) {
            return;
        }
        //文件夹
        Arrays.stream(psiDirectory.getSubdirectories()).forEach(this::findXml);
        //文件
        for (PsiFile psiFile : psiDirectory.getFiles()) {
            //处理xml文件
            if (psiFile instanceof XmlFile xmlFile) {
                XmlTag mapperTag = XmlUtil.getRootTagByName(xmlFile, Xml.MAPPER);
                if (null == mapperTag || !classFullName.equals(mapperTag.getAttributeValue(Xml.NAMESPACE))) {
                    continue;
                }
                //寻找标签
                findXml = true;
                List<XmlTag> tagList = XmlUtil.findTags(mapperTag, Xml.INSERT, Xml.UPDATE, Xml.DELETE, Xml.SELECT);
                for (XmlTag tag : tagList) {
                    Optional.ofNullable(tag.getAttributeValue(Xml.ID)).map(methodMap::get).ifPresent(method -> {
                        addLineMarker(method.getNameIdentifier(), tag);
                        methodMap.remove(method.getName());
                    });
                }
                //处理未找到跳转的方法
                methodMap.values().forEach(method -> {
                    Function<String, XmlTag> function = code -> {
                        XmlTag xmlTag = XmlElementFactory.getInstance(project).createTagFromText(code);
                        mapperTag.addSubTag(xmlTag, false);
                        return xmlTag;
                    };
                    addHandler(method, null, function, method.getNameIdentifier(), mapperTag);
                });
                return;
            }
        }
    }

    /**
     * 创建方法代码
     *
     * @param psiMethod     待补全的方法
     * @param annotation    注解
     * @param function      添加元素
     * @param sourceElement 图标元素
     * @param targetElement 目标元素
     */
    private <T extends PsiElement> void addHandler(PsiMethod psiMethod, PsiAnnotation annotation, Function<String, T> function, PsiElement sourceElement, PsiElement targetElement) {
        GutterIconNavigationHandler<PsiMethod> handler = (e, elt) -> ApplicationManager.getApplication().runWriteAction(() -> {
            //模版数据
            String methodName = Empty.of(annotation).map(t -> MyPsiUtil.getAnnotationValue(annotation, Annotation.METHOD)).orElse(psiMethod.getName());
            PsiMethodModel methodModel = new PsiMethodModel(methodName, psiMethod.getReturnType());
            List<PsiParameterModel> modelList = Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameterModel::new).collect(Collectors.toList());
            methodModel.setParameterModelList(modelList);
            Optional<String> sqlType = SQL_TYPE_MAP.entrySet().stream().filter(t -> methodName.startsWith(t.getKey())).map(Map.Entry::getValue).findAny();
            methodModel.setSqlType(sqlType.orElse(Xml.SELECT));
            //生成代码
            String code = TemplateFactory.getInstance().getTemplateContent(templateName, JsonUtil.toMap(methodModel));
            T newElement = function.apply(code);
            CodeStyleManager.getInstance(project).reformat(newElement);
            MyPsiUtil.moveToPsiElement(targetElement, -newElement.getTextLength());
        });
        addLineMarkerInfo(psiMethod, sourceElement, handler);
    }

}
