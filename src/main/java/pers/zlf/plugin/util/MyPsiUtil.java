package pers.zlf.plugin.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.util.lambda.Empty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/26 16:25
 */
public class MyPsiUtil {

    /**
     * 判断类是不是feign
     *
     * @param psiClass 待判断的类
     * @return boolean
     */
    public static boolean isFeign(PsiClass psiClass) {
        if (null == psiClass || !psiClass.isInterface()) {
            return false;
        }
        return psiClass.getAnnotation(Annotation.OPEN_FEIGN_CLIENT) != null || psiClass.getAnnotation(Annotation.NETFLIX_FEIGN_CLIENT) != null;
    }

    /**
     * 判断类是不是Controller
     *
     * @param psiClass 待判断的类
     * @return boolean
     */
    public static boolean isController(PsiClass psiClass) {
        if (psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return false;
        }
        List<String> controllerList = List.of(Annotation.CONTROLLER_1, Annotation.CONTROLLER_2, Annotation.REST_CONTROLLER);
        return Arrays.stream(psiClass.getAnnotations()).map(PsiAnnotation::getQualifiedName).anyMatch(controllerList::contains);
    }

    /**
     * 判断类是不是Controller
     *
     * @param psiClass              待判断的类
     * @param gatewayModuleNameList 排除的模块
     * @return boolean
     */
    public static boolean isController(PsiClass psiClass, List<String> gatewayModuleNameList) {
        if (null == psiClass) {
            return false;
        }
        String moduleName = getModuleName(psiClass.getContainingFile().getVirtualFile(), psiClass.getProject());
        if (StringUtil.isEmpty(moduleName) || gatewayModuleNameList.stream().anyMatch(moduleName::equals)) {
            return false;
        }
        return isController(psiClass);
    }

    /**
     * 查找指定注解
     *
     * @param annotations        带查找的注解
     * @param annotationNameList 注解名
     * @return 属性值
     */
    public static PsiAnnotation findAnnotation(PsiAnnotation[] annotations, List<String> annotationNameList) {
        return Arrays.stream(annotations).filter(a -> null != a.getQualifiedName() && annotationNameList.contains(a.getQualifiedName())).findAny().orElse(null);
    }

    /**
     * 获取注解属性值
     *
     * @param annotation    注解
     * @param attributeName 属性
     * @return 属性值
     */
    public static String getAnnotationValue(PsiAnnotation annotation, String attributeName) {
        if (null == annotation) {
            return Common.BLANK_STRING;
        }
        PsiAnnotationMemberValue value = annotation.findAttributeValue(attributeName);
        if (value == null) {
            return Common.BLANK_STRING;
        }
        //TODO 获取实际值
        String attributeValue = value.getText();
        if (attributeValue.startsWith(Common.LEFT_BRACE)) {
            attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
        }
        return attributeValue.replaceAll(Regex.DOUBLE_QUOTES, Common.BLANK_STRING).trim();
    }

    /**
     * 处理变量名，重名在末尾加数字
     *
     * @param variableName     待处理的变量名
     * @param psiType          变量类型
     * @param variableNameList 已存在的变量
     * @return 处理后的变量名
     */
    public static String dealVariableName(String variableName, PsiType psiType, List<String> variableNameList) {
        String basicTypeName = psiType.getPresentableText();
        String suggestedVariableName = Common.BLANK_STRING;
        if (basicTypeName.contains(Common.LESS_THAN_SIGN)) {
            String[] suggestedVariableNames = StringUtil.getFirstMatcher(basicTypeName, Regex.ANGLE_BRACKETS).split(Common.COMMA);
            suggestedVariableName = suggestedVariableNames[suggestedVariableNames.length - 1].trim();
            basicTypeName = basicTypeName.split(Common.LESS_THAN_SIGN)[0];
        } else if (basicTypeName.contains(Common.LEFT_BRACKETS)) {
            suggestedVariableName = basicTypeName.split(Regex.LEFT_BRACKETS)[0];
            basicTypeName = Common.S_STR;
        } else if (TypeUtil.isSimpleType(basicTypeName)) {
            basicTypeName = Common.BLANK_STRING;
        }
        suggestedVariableName = (TypeUtil.isSimpleType(suggestedVariableName) ? Common.BLANK_STRING : StringUtil.toLowerCaseFirst(suggestedVariableName)) + basicTypeName;
        if (suggestedVariableName.contains(variableName)) {
            variableName = suggestedVariableName;
        } else if (StringUtil.toLowerCaseFirst(basicTypeName).contains(variableName)) {
            variableName = basicTypeName;
        } else {
            variableName = variableName + basicTypeName;
        }
        String val = StringUtil.toLowerCaseFirst(variableName);
        int num = 1;
        while (variableNameList.contains(val)) {
            val = variableName + num;
            num++;
        }
        return val;
    }

    /**
     * 获取泛型类
     *
     * @param psiType 类型
     * @return List<User> return User
     */
    public static PsiClass getReferenceTypeClass(PsiType psiType) {
        if (psiType instanceof PsiClassReferenceType) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) psiType;
            PsiType[] psiTypeArr = referenceType.getParameters();
            if (psiTypeArr.length == 1) {
                return PsiUtil.resolveClassInClassTypeOnly(psiTypeArr[0]);
            }
        }
        return null;
    }

    /**
     * 获取类的方法，排除当前所在的方法
     *
     * @param psiClass     当前类
     * @param method       当前方法
     * @param containsName 方法名包含的字符
     * @return 方法数组
     */
    public static PsiMethod[] getMethods(PsiClass psiClass, PsiMethod method, String containsName) {
        LinkedList<PsiMethod> classMethodList = Arrays.stream(psiClass.getMethods()).collect(Collectors.toCollection(LinkedList::new));
        Iterator<PsiMethod> iterator = classMethodList.iterator();
        while (iterator.hasNext()) {
            PsiMethod classMethod = iterator.next();
            if (StringUtil.isNotEmpty(containsName) && !classMethod.getName().contains(containsName)) {
                iterator.remove();
                continue;
            }
            if (isSameMethod(classMethod, method)) {
                iterator.remove();
                break;
            }
        }
        return classMethodList.toArray(new PsiMethod[0]);
    }

    /**
     * 判断是否是同一个方法
     *
     * @param method1 方法1
     * @param method2 方法2
     * @return boolean
     */
    public static boolean isSameMethod(PsiMethod method1, PsiMethod method2) {
        // 判断方法名称是否相同
        if (null == method1 || null == method2 || !method1.getName().equals(method2.getName())) {
            return false;
        }
        String methodClassName1 = ((PsiClass) method1.getParent()).getQualifiedName();
        String methodClassName2 = ((PsiClass) method2.getParent()).getQualifiedName();
        if (null != methodClassName1 && !methodClassName1.equals(methodClassName2)) {
            return false;
        }
        // 判断方法参数列表是否相同
        PsiParameter[] paramArr1 = method1.getParameterList().getParameters();
        PsiParameter[] paramArr2 = method2.getParameterList().getParameters();
        if (paramArr1.length != paramArr2.length) {
            return false;
        }
        for (int i = 0; i < paramArr1.length; i++) {
            if (!isSamePsiType(paramArr1[i].getType(), paramArr2[i].getType())) {
                return false;
            }
        }
        // 判断返回类型是否相同
        return isSamePsiType(method1.getReturnType(), method2.getReturnType());
    }

    /**
     * 判断是否是同一类型
     *
     * @param psiType1 类型1
     * @param psiType2 类型2
     * @return boolean
     */
    public static boolean isSamePsiType(PsiType psiType1, PsiType psiType2) {
        if ((psiType1 == null && psiType2 != null) || (psiType1 != null && psiType2 == null)) {
            return false;
        }
        if (psiType1 == null) {
            return true;
        }
        return psiType1.getInternalCanonicalText().equals(psiType2.getInternalCanonicalText());
    }

    /**
     * 获取当前模块路径
     *
     * @param virtualFile 模块文件
     * @param project     项目
     * @return 模块路径
     */
    public static Path getCurrentModulePath(VirtualFile virtualFile, Project project) {
        String filePathStr = Empty.of(virtualFile).map(VirtualFile::getPath).orElse(Common.BLANK_STRING);
        Path filePath = Paths.get(filePathStr);
        if (StringUtil.isEmpty(filePathStr)) {
            return filePath;
        }
        Path projectPath = Paths.get(Optional.ofNullable(project.getBasePath()).orElse(Common.BLANK_STRING));
        return getSameDirectoryPath(projectPath, filePath);
    }

    /**
     * 根据VirtualFile 获取所属模块的名称
     *
     * @param virtualFile virtualFile
     * @param project     project
     * @return String
     */
    public static String getModuleName(VirtualFile virtualFile, Project project) {
        return getCurrentModulePath(virtualFile, project).getFileName().toString();
    }

    /**
     * 获取同文件夹下的路径
     *
     * @param path1 路径1
     * @param path2 路径2
     * @return Path
     */
    public static Path getSameDirectoryPath(Path path1, Path path2) {
        if (path1 == null || path2 == null || path1.getParent() == null || path2.getParent() == null) {
            return Path.of(Common.BLANK_STRING);
        }
        if (path1.getParent().equals(path2.getParent())) {
            return path2;
        }
        return getSameDirectoryPath(path1, path2.getParent());
    }


    /**
     * 查找PsiClass
     *
     * @param globalSearchScope GlobalSearchScope
     * @param classFullName     class全名
     * @return Optional<PsiClass>
     */
    public static Optional<PsiClass> findClassByFullName(GlobalSearchScope globalSearchScope, String classFullName) {
        if (StringUtil.isEmpty(classFullName) || null == globalSearchScope || null == globalSearchScope.getProject()) {
            return Optional.empty();
        }
        return Optional.ofNullable(JavaPsiFacade.getInstance(globalSearchScope.getProject()).findClass(classFullName, globalSearchScope));
    }

    /**
     * 获取元素的注释
     *
     * @param element 元素
     * @return 注释
     */
    public static String getComment(PsiElement element) {
        StringBuilder comment = new StringBuilder(Common.BLANK_STRING);
        for (PsiElement childrenElement : element.getChildren()) {
            if (childrenElement instanceof PsiDocComment) {
                PsiDocComment docComment = (PsiDocComment) childrenElement;
                String value = Arrays.stream(docComment.getDescriptionElements()).map(PsiElement::getText).collect(Collectors.joining());
                value = Arrays.stream(value.split(Regex.WRAP)).filter(StringUtil::isNotEmpty).collect(Collectors.joining(Common.SPACE));
                comment.append(value);
            } else if (childrenElement instanceof PsiComment) {
                comment.append(childrenElement.getText());
            }
        }
        return comment.toString().trim();
    }

    /**
     * 获取元素带标签的注释
     *
     * @param element 元素
     * @return 注释
     */
    public static Map<String, String> getParamComment(PsiElement element) {
        Map<String, String> commentMap = new HashMap<>(2);
        for (PsiElement childrenElement : element.getChildren()) {
            if (childrenElement instanceof PsiDocComment) {
                PsiDocComment docComment = (PsiDocComment) childrenElement;
                for (PsiDocTag tag : docComment.getTags()) {
                    Optional.ofNullable(tag.getValueElement()).map(PsiDocTagValue::getText).ifPresent(paramName -> {
                        PsiElement[] dataElementArr = tag.getDataElements();
                        String paramComment = paramName;
                        if (dataElementArr.length != 0) {
                            paramComment = IntStream.range(1, dataElementArr.length - 1).mapToObj(i -> dataElementArr[i].getText())
                                    .filter(StringUtil::isNotEmpty).collect(Collectors.joining(Common.COMMA));
                        }
                        commentMap.put(paramName, paramComment);
                    });
                }
            }
        }
        return commentMap;
    }

    /**
     * 获取文件类型
     *
     * @param virtualFile virtualFile
     * @return String
     */
    public static String getFileType(VirtualFile virtualFile) {
        String[] val = virtualFile.getName().split(Regex.DOT);
        if (val.length > 1) {
            return Common.DOT + val[val.length - 1];
        }
        return Common.DOT + virtualFile.getFileType().getName();
    }

    /**
     * 把编辑器移至psiElement
     *
     * @param psiElement 目标元素
     * @param offset     偏移量
     */
    public static void moveToPsiElement(PsiElement psiElement, int offset) {
        Project project = psiElement.getProject();
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, psiElement.getContainingFile().getVirtualFile());
        // 打开文件
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
        if (editor != null) {
            // 移动到元素 TODO 有一点点误差
            editor.getCaretModel().moveToOffset(psiElement.getTextRangeInParent().getEndOffset() + offset);
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_DOWN);
            editor.getSelectionModel().removeSelection();
        }
    }

    /**
     * 获取当前元素所在模块的资源文件
     *
     * @param element 元素
     * @return VirtualFile[]
     */
    public static VirtualFile[] getModuleSourceRoots(PsiElement element) {
        return Optional.ofNullable(element.getContainingFile())
                .map(PsiFile::getVirtualFile)
                .map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, element.getProject()))
                .map(ModuleRootManager::getInstance)
                .map(ModuleRootManager::getSourceRoots)
                .orElse(new VirtualFile[0]);
    }

    /**
     * 获取代码块中的指定类型元素
     *
     * @param codeBlock 代码块
     * @param function  具体逻辑
     * @return List<T>
     */
    public static <T> List<T> getElementFromPsiCodeBlock(PsiCodeBlock codeBlock, Function<PsiElement, List<T>> function) {
        List<T> elementList = new ArrayList<>();
        if (codeBlock == null) {
            return elementList;
        }
        for (PsiElement element : codeBlock.getChildren()) {
            //获取元素
            Optional.ofNullable(function.apply(element)).ifPresent(elementList::addAll);
            if (element instanceof PsiStatement) {
                PsiStatement statement = (PsiStatement) element;
                //递归代码块
                Arrays.stream(statement.getChildren()).filter(t -> t instanceof PsiBlockStatement)
                        .map(t -> (PsiBlockStatement) t).forEach(blockStatement -> elementList.addAll(getElementFromPsiCodeBlock(blockStatement.getCodeBlock(), function)));
            }
        }
        //获取元素
        return elementList;
    }

    /**
     * 判断当前元素是否在新行
     *
     * @param psiElement 当前元素
     * @return boolean
     */
    public static boolean isNewLine(PsiElement psiElement) {
        return Optional.ofNullable(PsiTreeUtil.prevVisibleLeaf(psiElement)).map(t -> Common.SEMICOLON.equals(t.getText()) || Common.LEFT_BRACE.equals(t.getText()) || Common.RIGHT_BRACE.equals(t.getText()))
                .orElse(false);
    }

    /**
     * 获取类的常规字段
     *
     * @param psiClass 类
     * @return List<PsiField>
     */
    public static List<PsiField> getPsiFieldList(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields()).filter(field -> !field.hasModifierProperty(PsiModifier.STATIC) && !field.hasModifierProperty(PsiModifier.FINAL) && !field.hasModifierProperty(PsiModifier.PUBLIC))
                .collect(Collectors.toList());
    }

    /**
     * 导入类
     *
     * @param psiFile          所在文件
     * @param classFullNameArr 待导入类
     */
    public static void importClass(PsiFile psiFile, String... classFullNameArr) {
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            for (String classFullName : classFullNameArr) {
                MyPsiUtil.findClassByFullName(javaFile.getResolveScope(), classFullName).ifPresent(javaFile::importClass);
            }
        }
    }

}
