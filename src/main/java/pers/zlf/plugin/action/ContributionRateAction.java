package pers.zlf.plugin.action;

import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.util.CodeCountUtil;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class ContributionRateAction extends BaseAction {
    private final String USER = "user";
    private final String EMAIL = "email";
    /** 项目路径 */
    private Path bathPath;
    /** Git repository */
    private Repository repository;
    /** 总行数 */
    private int totalLineCount;
    /** 我的行数 */
    private int myLineCount;
    /** 参与统计的文件类型 */
    private List<String> fileTypeList;
    /** 参与统计的git账号 */
    private List<String> emailList;

    @Override
    protected boolean isVisible() {
        return null != module;
    }

    @Override
    protected boolean isExecute() {
        if (CodeLinesCountDecorator.contributionRateIsRunning) {
            Message.notifyError(project, Message.STATISTICS_IN_PROGRESS);
            return false;
        }
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList())) {
            Message.notifyError(project, Message.PLEASE_CONFIGURE_FILE_TYPE_LIST_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_ID_CODE_STATISTICS);
            return false;
        }
        return true;
    }

    @Override
    protected void execute() {
        //TODO 改用官方git4idea实现
        //获取配置
        fileTypeList = ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList();
        emailList = ConfigFactory.getInstance().getCodeStatisticsConfig().getGitEmailList();
        CodeLinesCountDecorator.clearContributionRate(project.getName());
        ThreadPoolFactory.CODE_STATISTICS_POOL.execute(() -> {
            CodeLinesCountDecorator.contributionRateIsRunning = true;
            for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
                totalLineCount = 0;
                myLineCount = 0;
                //模块路径
                bathPath = Paths.get(virtualFile.getPath());
                //默认当前分支
                try {
                    String gitPath = Paths.get(bathPath.toString(), Common.DOT_GIT).toString();
                    repository = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
                } catch (IOException e) {
                    continue;
                }
                //没有配置取当前邮箱
                if (CollectionUtil.isEmpty(emailList)) {
                    emailList = Empty.of(repository.getConfig().getString(USER, null, EMAIL)).map(List::of).orElse(new ArrayList<>());
                    if (CollectionUtil.isEmpty(emailList)) {
                        continue;
                    }
                }
                this.dealDirectory(virtualFile);
                if (totalLineCount != 0) {
                    CodeLinesCountDecorator.updateContributionRate(module.getName(), totalLineCount, myLineCount);
                    //更新贡献率行数
                    CodeLinesCountDecorator.updateNode();
                }
            }
            CodeLinesCountDecorator.contributionRateIsRunning = false;
        });
    }

    private void dealDirectory(VirtualFile virtualFile) {
        //处理文件夹
        if (virtualFile.isDirectory()) {
            Arrays.stream(virtualFile.getChildren()).forEach(this::dealDirectory);
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
            BlameResult result = new BlameCommand(repository).setFilePath(filePath.replaceAll(Regex.BACKSLASH, Common.SLASH)).call();
            if (null == result) {
                return;
            }
            CommentFormat commentFormat = CodeCountUtil.getCommentFormat(virtualFile);
            RawText rawText = result.getResultContents();
            int length = rawText.size();
            for (int i = 0; i < length; i++) {
                if (CodeCountUtil.count(rawText.getString(i), commentFormat)) {
                    totalLineCount++;
                    if (emailList.contains(result.getSourceAuthor(i).getEmailAddress())) {
                        //自己提交的
                        myLineCount++;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
