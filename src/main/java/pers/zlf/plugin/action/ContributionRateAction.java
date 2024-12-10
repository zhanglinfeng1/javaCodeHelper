package pers.zlf.plugin.action;

import com.intellij.openapi.roots.ProjectRootManager;
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

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class ContributionRateAction extends BaseAction {
    private final String USER = "user";
    private final String EMAIL = "email";
    private final String DOT_GIT = ".git";
    /** 项目路径 */
    private Path bathPath;
    /** Git repository */
    private Repository repository;
    /** 总行数 */
    private int totalLineCount = 0;
    /** 我的行数 */
    private int myLineCount = 0;

    @Override
    public boolean isVisible() {
        return null != project;
    }

    @Override
    public boolean isExecute() {
        if (CodeLinesCountDecorator.isRunning){
            Message.showMessage(Message.STATISTICS_IN_PROGRESS);
            return false;
        }
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList())) {
            Message.showMessage(Message.CODE_STATISTICAL_CONFIGURATION);
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        //获取配置
        List<String> fileTypeList = ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList();
        //获取项目路径
        String projectPath = Empty.of(project.getBasePath()).map(Paths::get).map(Path::getParent).map(Path::toString).orElse(null);
        if (StringUtil.isEmpty(projectPath)) {
            return;
        }
        CodeLinesCountDecorator.clearContributionRate(project.getName());
        ThreadPoolFactory.CODE_STATISTICS_POOL.execute(() -> {
            List<String> myEmailList = ConfigFactory.getInstance().getCodeStatisticsConfig().getGitEmailList();
            for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                totalLineCount = 0;
                myLineCount = 0;
                //项目路径
                String moduleName = MyPsiUtil.getModuleName(virtualFile, project);
                bathPath = Paths.get(projectPath, moduleName);
                //默认当前分支
                try {
                    String gitPath = Paths.get(bathPath.toString(), DOT_GIT).toString();
                    repository = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
                } catch (IOException e) {
                    continue;
                }
                //没有配置取当前邮箱
                if (CollectionUtil.isEmpty(myEmailList)) {
                    myEmailList = Empty.of(repository.getConfig().getString(USER, null, EMAIL)).map(List::of).orElse(new ArrayList<>());
                    if (CollectionUtil.isEmpty(myEmailList)) {
                        continue;
                    }
                }
                this.dealDirectory(virtualFile, fileTypeList, myEmailList);
                if (totalLineCount != 0) {
                    CodeLinesCountDecorator.updateContributionRate(moduleName, totalLineCount, myLineCount);
                    //更新贡献率行数
                    CodeLinesCountDecorator.updateNode();
                }
            }
            CodeLinesCountDecorator.isRunning = false;
        });
    }

    private void dealDirectory(VirtualFile virtualFile, List<String> fileTypeList, List<String> myEmailList) {
        //处理文件夹
        if (virtualFile.isDirectory()) {
            Arrays.stream(virtualFile.getChildren()).forEach(subFile -> this.dealDirectory(subFile, fileTypeList, myEmailList));
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
            filePath = filePath.replaceAll(Regex.BACKSLASH, Common.SLASH);
            BlameResult result = new BlameCommand(repository).setFilePath(filePath).call();
            if (null == result) {
                return;
            }
            CommentFormat commentFormat = CodeLineCountAction.getCommentFormat(virtualFile);
            RawText rawText = result.getResultContents();
            int length = rawText.size();
            for (int i = 0; i < length; i++) {
                if (CodeLineCountAction.count(rawText.getString(i), commentFormat)) {
                    totalLineCount++;
                    if (myEmailList.contains(result.getSourceAuthor(i).getEmailAddress())) {
                        //自己提交的
                        myLineCount++;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
