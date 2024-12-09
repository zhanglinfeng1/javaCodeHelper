package pers.zlf.plugin.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BaseAction {
    private static final List<String> JAVA_COMMENT = List.of("//");
    private static final List<String> JAVA_COMMENT_PREFIX = List.of("/*");
    private static final List<String> JAVA_COMMENT_SUFFIX = List.of("*/");
    private static final List<String> XML_COMMENT_PREFIX = List.of("<!--", "<![CDATA[");
    private static final List<String> XML_COMMENT_SUFFIX = List.of("-->", "]]>");

    @Override
    public boolean isVisible() {
        return null != project;
    }

    @Override
    public boolean isExecute() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList())) {
            Message.showMessage(Message.CODE_STATISTICAL_CONFIGURATION);
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        countCodeLines(project);
    }

    /**
     * 统计项目的代码行数
     *
     * @param project 文件或文件夹
     */
    public static void countCodeLines(Project project) {
        String projectName = project.getName();
        CodeLinesCountDecorator.clearLineCount(projectName);
        //按模块统计
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            CodeLinesCountDecorator.updateLineCount(project, virtualFile, getLineCount(virtualFile));
        }
        //处理统计结果
        CodeLinesCountDecorator.updateNode();
    }

    /**
     * 统计代码行数
     *
     * @param virtualFile 文件或文件夹
     * @return int
     */
    public static int getLineCount(VirtualFile virtualFile) {
        //获取配置
        List<String> fileTypeList = ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList();
        if (virtualFile.isDirectory()) {
            return Arrays.stream(virtualFile.getChildren()).mapToInt(CodeLineCountAction::getLineCount).sum();
        }
        String fileType = MyPsiUtil.getFileType(virtualFile);
        //是否为参与统计的文件类型
        if (fileTypeList.stream().anyMatch(fileType::equalsIgnoreCase)) {
            CommentFormat commentFormat = getCommentFormat(virtualFile);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(virtualFile.getInputStream()))) {
                return (int) br.lines().filter(line -> count(line, commentFormat)).count();
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    /**
     * 根据文件类型，获取注释格式
     *
     * @param virtualFile virtualFile
     * @return CommentType
     */
    public static CommentFormat getCommentFormat(VirtualFile virtualFile) {
        String fileType = MyPsiUtil.getFileType(virtualFile);
        return switch (fileType) {
            case FileType.JAVA_FILE -> new CommentFormat(FileType.JAVA_FILE, JAVA_COMMENT, JAVA_COMMENT_PREFIX, JAVA_COMMENT_SUFFIX);
            case FileType.XML_FILE -> new CommentFormat(FileType.XML_FILE, new ArrayList<>(), XML_COMMENT_PREFIX, XML_COMMENT_SUFFIX);
            default -> new CommentFormat();
        };
    }

    /**
     * 判断是否统计
     *
     * @param lineValue     行内容
     * @param commentFormat 注释格式
     * @return boolean
     */
    public static boolean count(String lineValue, CommentFormat commentFormat) {
        //统计空行
        if (StringUtil.isEmpty(lineValue)) {
            return ConfigFactory.getInstance().getCodeStatisticsConfig().isCountEmptyLine();
        }
        //关键字统计
        //java中的package、import
        if (FileType.JAVA_FILE.equals(commentFormat.getFileType())) {
            if (lineValue.startsWith(Keyword.JAVA_PACKAGE + Common.SPACE) || lineValue.startsWith(Keyword.JAVA_IMPORT + Common.SPACE)) {
                return ConfigFactory.getInstance().getCodeStatisticsConfig().isCountKeyword();
            }
        }
        //注释统计
        if (StringUtil.isComment(lineValue, commentFormat)) {
            return ConfigFactory.getInstance().getCodeStatisticsConfig().isCountComment();
        }
        return true;
    }
}
