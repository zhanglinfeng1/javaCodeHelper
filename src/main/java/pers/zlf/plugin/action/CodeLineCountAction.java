package pers.zlf.plugin.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.MESSAGE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommentCheckResult;
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
 * @Author zhanglinfeng
 * @Date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BasicAction {

    @Override
    public boolean check() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCommonConfig().getFileTypeList())) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(MESSAGE.CODE_STATISTICAL_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        return true;
    }

    @Override
    public void action() {
        CodeLinesCountDecorator.clearLineCount();
        //按模块统计
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            int count = getLineCount(virtualFile);
            if (count == 0) {
                continue;
            }
            String moduleName = MyPsiUtil.getModuleName(virtualFile, project);
            CodeLinesCountDecorator.updateLineCount(moduleName, count);
        }
        //处理统计结果
        CodeLinesCountDecorator.updateNode();
    }

    /**
     * 统计代码行数
     *
     * @param virtualFile  文件或文件夹
     * @return int
     */
    public static int getLineCount(VirtualFile virtualFile) {
        //获取配置
        List<String> fileTypeList = ConfigFactory.getInstance().getCommonConfig().getFileTypeList();
        if (virtualFile.isDirectory()) {
            return Arrays.stream(virtualFile.getChildren()).mapToInt(CodeLineCountAction::getLineCount).sum();
        }
        String fileType = COMMON.DOT + virtualFile.getFileType().getName();
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
        //java文件
        if (virtualFile.getFileType() instanceof JavaFileType) {
            return new CommentFormat(COMMON.JAVA_COMMENT, COMMON.JAVA_COMMENT_PREFIX, COMMON.JAVA_COMMENT_SUFFIX);
        } else if (virtualFile.getFileType() instanceof XmlFileType) {
            //xml 文件
            return new CommentFormat(new ArrayList<>(), COMMON.XML_COMMENT_PREFIX, COMMON.XML_COMMENT_SUFFIX);
        }
        return new CommentFormat();
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
            CommentCheckResult commentCheckResult = new CommentCheckResult();
            return (int) lineList.stream().filter(line -> !StringUtil.isComment(line, commentFormat, commentCheckResult)).count();
        } catch (Exception ignored) {
        }
        return 0;
    }
}
