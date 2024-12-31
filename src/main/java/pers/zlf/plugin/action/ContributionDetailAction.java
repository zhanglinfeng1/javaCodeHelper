package pers.zlf.plugin.action;

import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.dialog.ContributionDetailDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommentFormat;
import pers.zlf.plugin.pojo.ContributionDetail;
import pers.zlf.plugin.util.CodeCountUtil;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.DateUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.SwingUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class ContributionDetailAction extends BaseAction {
    /** 项目路径 */
    private Path bathPath;
    /** Git repository */
    private Repository repository;
    /** 参与统计的文件类型 */
    private List<String> fileTypeList;
    /** 参与统计的日期 */
    private Date countDate;
    /** key:文件名   value{key:git邮箱  value：详情} */
    private final Map<String, Map<String, ContributionDetail>> totalContributionDetailMap = new HashMap<>();

    @Override
    protected boolean isVisible() {
        return null != module;
    }

    @Override
    protected boolean isExecute() {
        if (CodeLinesCountDecorator.contributionDetailIsRunning) {
            Message.notifyError(project, Message.STATISTICS_IN_PROGRESS);
            return false;
        }
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList())) {
            Message.notifyError(project, Message.PLEASE_CONFIGURE_FILE_TYPE_LIST_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_CODE_STATISTICS_ID);
            return false;
        }
        return true;
    }

    @Override
    protected void execute() {
        //TODO 改用官方git4idea实现
        //获取配置
        fileTypeList = ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList();
        countDate = Empty.of(ConfigFactory.getInstance().getCodeStatisticsConfig().getCountDate()).map(t -> DateUtil.parse(t, DateUtil.YYYY_MM_DD)).orElse(null);
        totalContributionDetailMap.clear();
        ThreadPoolFactory.CODE_STATISTICS_POOL.execute(() -> {
            CodeLinesCountDecorator.contributionDetailIsRunning = true;
            for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
                //模块路径
                bathPath = Paths.get(virtualFile.getPath());
                //默认当前分支
                try {
                    String gitPath = Paths.get(bathPath.toString(), Common.DOT_GIT).toString();
                    repository = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
                } catch (IOException e) {
                    continue;
                }
                this.dealDirectory(virtualFile);
            }
            CodeLinesCountDecorator.contributionDetailIsRunning = false;
            SwingUtil.registerToolWindow(project, Common.TOOL_WINDOW_ID_CODE_STATISTICS_DETAILS, new ContributionDetailDialog(project, module.getName(), totalContributionDetailMap).getContent(), module.getName());
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
            String fileName = virtualFile.getName();
            String fileFullName = filePath.replaceAll(Regex.BACKSLASH, Common.DOT) + fileName;
            BlameResult result = new BlameCommand(repository).setFilePath(filePath.replaceAll(Regex.BACKSLASH, Common.SLASH)).call();
            if (null == result) {
                return;
            }
            Map<String, ContributionDetail> contributionDetailMap = statistics(result, virtualFile);
            if (contributionDetailMap.isEmpty()){
                return;
            }
            if (totalContributionDetailMap.containsKey(fileName)) {
                totalContributionDetailMap.put(fileFullName, contributionDetailMap);
            } else {
                totalContributionDetailMap.put(fileName, contributionDetailMap);
            }
        } catch (Exception ignored) {
        }
    }

    private Map<String, ContributionDetail> statistics(BlameResult result, VirtualFile virtualFile) {
        Map<String, ContributionDetail> contributionDetailMap = new HashMap<>();
        CommentFormat commentFormat = CodeCountUtil.getCommentFormat(virtualFile);
        RawText rawText = result.getResultContents();
        int length = rawText.size();
        for (int i = 0; i < length; i++) {
            PersonIdent personIdent = result.getSourceAuthor(i);
            if (countDate != null) {
                Date date = personIdent.getWhen();
                if (countDate.after(date)) {
                    continue;
                }
            }
            // 按git邮箱统计
            String email = personIdent.getEmailAddress();
            if (!contributionDetailMap.containsKey(email)) {
                contributionDetailMap.put(email, new ContributionDetail(email, personIdent.getName()));
            }
            ContributionDetail contributionDetail = contributionDetailMap.get(email);
            String lineValue = rawText.getString(i);
            if (StringUtil.isEmpty(lineValue)) {
                contributionDetail.setEmptyLineCount(contributionDetail.getEmptyLineCount() + 1);
            } else if (StringUtil.isComment(lineValue, commentFormat)) {
                contributionDetail.setCommentCount(contributionDetail.getCommentCount() + 1);
            } else if (StringUtil.isKeyWord(commentFormat.getFileType(), lineValue)) {
                contributionDetail.setKeywordCount(contributionDetail.getKeywordCount() + 1);
            } else {
                contributionDetail.setCodeCount(contributionDetail.getCodeCount() + 1);
            }
        }
        return contributionDetailMap;
    }
}
