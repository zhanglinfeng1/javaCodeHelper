package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
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
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BaseAction {

    @Override
    public boolean check() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCommonConfig().getFileTypeList())) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(Message.CODE_STATISTICAL_CONFIGURATION, Common.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        return true;
    }

    @Override
    public void action() {
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
        List<String> fileTypeList = ConfigFactory.getInstance().getCommonConfig().getFileTypeList();
        if (virtualFile.isDirectory()) {
            return Arrays.stream(virtualFile.getChildren()).mapToInt(CodeLineCountAction::getLineCount).sum();
        }
        String fileType = MyPsiUtil.getFileType(virtualFile);
        if (fileTypeList.stream().anyMatch(fileType::equalsIgnoreCase)) {
            CommentFormat commentFormat = getCommentFormat(virtualFile);
            return getLineCount(virtualFile, commentFormat);
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
        switch (fileType) {
            case ClassType.JAVA_FILE:
                return new CommentFormat(Common.JAVA_COMMENT, Common.JAVA_COMMENT_PREFIX, Common.JAVA_COMMENT_SUFFIX);
            case ClassType.XML_FILE:
                return new CommentFormat(new ArrayList<>(), Common.XML_COMMENT_PREFIX, Common.XML_COMMENT_SUFFIX);
            default:
                return new CommentFormat();
        }
    }

    /**
     * 统计代码行数
     *
     * @param virtualFile   具体文件
     * @param commentFormat 注释格式
     * @return int
     */
    private static int getLineCount(VirtualFile virtualFile, CommentFormat commentFormat) {
        //是否统计注释
        boolean countComment = ConfigFactory.getInstance().getCommonConfig().isCountComment();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(virtualFile.getInputStream()))) {
            List<String> lineList = br.lines().filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            //统计注释
            if (countComment) {
                return lineList.size();
            }
            //不统计注释
            return (int) lineList.stream().filter(line -> !StringUtil.isComment(line, commentFormat)).count();
        } catch (Exception ignored) {
        }
        return 0;
    }
}
