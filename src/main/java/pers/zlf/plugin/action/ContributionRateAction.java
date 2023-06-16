package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ProjectRootManager;
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
import pers.zlf.plugin.pojo.CommentCheckResult;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MathUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/14 11:48
 */
public class ContributionRateAction extends BasicAction {
    /** 项目路径 */
    private Path bathPath;
    /** Git repository */
    private Repository repository;
    /** 参与统计的文件类型 */
    private List<String> fileTypeList;
    /** 统计注释 */
    private boolean countComment;
    /** Git 邮箱 */
    private List<String> myEmailList;
    /** 总行数 */
    private int totalLineCount = 0;
    /** 我的行数 */
    private int myLineCount = 0;

    @Override
    public boolean check() {
        //获取配置
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        fileTypeList = commonConfig.getFileTypeList();
        if (CollectionUtil.isEmpty(fileTypeList)) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(MESSAGE.CODE_STATISTICAL_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        myEmailList = commonConfig.getGitEmailList();
        countComment = commonConfig.isCountComment();
        return true;
    }

    @Override
    public void action() {
        CodeLinesCountDecorator.contributionRateMap.clear();
        //开始统计
        Map<String, Integer> totalLineCountMap = new HashMap<>();
        Map<String, Integer> myLineCountMap = new HashMap<>();
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            totalLineCount = 0;
            myLineCount = 0;
            String moduleName = MyPsiUtil.getModuleNameByVirtualFile(virtualFile, project);
            if (StringUtil.isEmpty(moduleName)) {
                continue;
            }
            bathPath = Paths.get(virtualFile.getPath().substring(0, virtualFile.getPath().indexOf(moduleName)), moduleName);
            //默认当前分支
            try {
                String gitPath = Paths.get(bathPath.toString(), COMMON.GIT).toString();
                repository = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
            } catch (IOException e) {
                continue;
            }
            //没有配置取当前邮箱
            if (CollectionUtil.isEmpty(myEmailList)) {
                String myEmail = repository.getConfig().getString(COMMON.USER, null, COMMON.EMAIL);
                if (StringUtil.isEmpty(myEmail)) {
                    continue;
                }
                myEmailList = Collections.singletonList(myEmail);
            }
            this.dealDirectory(virtualFile, fileTypeList, countComment);
            totalLineCountMap.put(moduleName, totalLineCount + Optional.ofNullable(totalLineCountMap.get(moduleName)).orElse(0));
            myLineCountMap.put(moduleName, myLineCount + Optional.ofNullable(myLineCountMap.get(moduleName)).orElse(0));
        }
        //处理统计结果
        totalLineCountMap.forEach((moduleName, value) -> {
            if (0 == value) {
                return;
            }
            int myCount = myLineCountMap.get(moduleName);
            String contributionRate = MathUtil.percentage(myCount, value, 2) + COMMON.PERCENT_SIGN;
            CodeLinesCountDecorator.contributionRateMap.put(moduleName, contributionRate);
            //更新贡献率行数
            CodeLinesCountDecorator.updateNode();
        });
    }

    private void dealDirectory(VirtualFile virtualFile, List<String> fileTypeList, boolean countComment) {
        //处理文件夹
        if (virtualFile.isDirectory()) {
            for (VirtualFile subFile : virtualFile.getChildren()) {
                this.dealDirectory(subFile, fileTypeList, countComment);
            }
            return;
        }
        //判断文件类型
        String fileType = COMMON.DOT + virtualFile.getFileType().getName();
        if (fileTypeList.stream().noneMatch(fileType::equalsIgnoreCase)) {
            return;
        }
        //处理文件
        try {
            BlameCommand blameCommand = new BlameCommand(repository);
            //相对路径
            String filePath = bathPath.relativize(Path.of(virtualFile.getPath())).toString();
            filePath = filePath.replaceAll(REGEX.BACKSLASH, COMMON.SLASH);
            blameCommand.setFilePath(filePath);
            BlameResult result = blameCommand.call();
            if (null == result) {
                return;
            }
            CommentCheckResult commentCheckResult = new CommentCheckResult();
            CommentFormat commentFormat = MyPsiUtil.getCommentFormat(virtualFile);
            RawText rawText = result.getResultContents();
            for (int i = 0; i < rawText.size(); i++) {
                String line = rawText.getString(i);
                //空行不统计
                if (StringUtil.isEmpty(line)) {
                    continue;
                }
                // 不统计注释且当前行是注释
                if (!countComment && StringUtil.isComment(line, commentFormat, commentCheckResult)) {
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
