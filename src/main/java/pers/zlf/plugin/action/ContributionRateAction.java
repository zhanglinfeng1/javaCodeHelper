package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.MESSAGE;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/14 11:48
 */
public class ContributionRateAction extends BasicAction {
    /** 选中的模块 */
    private Module module;
    /** 选中的模块名 */
    private String moduleName;
    /** 项目路径 */
    private Path bathPath;
    /** Git repository */
    private Repository repository;
    /** 总行数 */
    private int totalLineCount = 0;
    /** 我的行数 */
    private int myLineCount = 0;

    @Override
    public boolean check() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCommonConfig().getFileTypeList())) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(MESSAGE.CODE_STATISTICAL_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        //选中文件的所属模块
        module = Optional.ofNullable(virtualFile).map(t -> ModuleUtil.findModuleForFile(t, project)).orElse(null);
        if (null == module) {
            return false;
        }
        moduleName = MyPsiUtil.getModuleName(virtualFile, project);
        bathPath = MyPsiUtil.getCurrentModulePath(virtualFile, project);
        return !StringUtil.isEmpty(moduleName);
    }

    @Override
    public void action() {
        //获取配置
        List<String> fileTypeList = ConfigFactory.getInstance().getCommonConfig().getFileTypeList();
        boolean countComment = ConfigFactory.getInstance().getCommonConfig().isCountComment();
        List<String> myEmailList = ConfigFactory.getInstance().getCommonConfig().getGitEmailList();
        //默认当前分支
        try {
            String gitPath = Paths.get(bathPath.toString(), COMMON.GIT).toString();
            repository = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
        } catch (IOException e) {
            return;
        }
        //没有配置取当前邮箱
        if (CollectionUtil.isEmpty(myEmailList)) {
            myEmailList = Empty.of(repository.getConfig().getString(COMMON.USER, null, COMMON.EMAIL)).map(List::of).orElse(new ArrayList<>());
            if (CollectionUtil.isEmpty(myEmailList)) {
                return;
            }
        }
        //统计模块的贡献率
        totalLineCount = 0;
        myLineCount = 0;
        for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
            this.dealDirectory(virtualFile, fileTypeList, countComment, myEmailList);
        }
        CodeLinesCountDecorator.updateContributionRate(moduleName, totalLineCount, myLineCount);
        //更新贡献率行数
        CodeLinesCountDecorator.updateNode();
    }

    private void dealDirectory(VirtualFile virtualFile, List<String> fileTypeList, boolean countComment, List<String> myEmailList) {
        //处理文件夹
        if (virtualFile.isDirectory()) {
            Arrays.stream(virtualFile.getChildren()).forEach(subFile -> this.dealDirectory(subFile, fileTypeList, countComment, myEmailList));
            return;
        }
        //判断文件类型
        String fileType = MyPsiUtil.getFileType(virtualFile);
        if (fileTypeList.stream().noneMatch(fileType::equalsIgnoreCase)) {
            return;
        }
        //处理文件
        try {
            //相对路径
            String filePath = bathPath.relativize(Paths.get(virtualFile.getPath())).toString();
            filePath = filePath.replaceAll(REGEX.BACKSLASH, COMMON.SLASH);
            BlameResult result = new BlameCommand(repository).setFilePath(filePath).call();
            if (null == result) {
                return;
            }
            CommentFormat commentFormat = CodeLineCountAction.getCommentFormat(virtualFile);
            RawText rawText = result.getResultContents();
            for (int i = 0; i < rawText.size(); i++) {
                String line = rawText.getString(i);
                //空行不统计、不统计注释且当前行是注释
                if (StringUtil.isEmpty(line) || (!countComment && StringUtil.isComment(line, commentFormat))) {
                    continue;
                }
                totalLineCount++;
                if (myEmailList.contains(result.getSourceAuthor(i).getEmailAddress())) {
                    //自己提交的
                    myLineCount++;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
