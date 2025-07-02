package pers.zlf.plugin.schedule;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;
import com.intellij.vcs.log.impl.VcsLogContentProvider;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.GitUtil;
import git4idea.fetch.GitFetchSupport;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 22:51
 */
public class NewCodeRemindSchedule extends Schedule {
    private Notification notification;

    public NewCodeRemindSchedule(Project project) {
        super(project);
    }

    @Override
    protected boolean isRun() {
        return ConfigFactory.getInstance().getCommonConfig().isOpenCodeRemind();
    }

    @Override
    protected int getRemindMinute() {
        return ConfigFactory.getInstance().getCommonConfig().getCodeRemindMinute();
    }

    @Override
    public void run() {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(PROJECT);
        List<GitRepository> repositories = repositoryManager.getRepositories();
        //执行fetch
        GitFetchSupport.fetchSupport(PROJECT).fetchAllRemotes(repositories);
        for (Module module : ModuleManager.getInstance(PROJECT).getModules()) {
            for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
                GitRepository repository = repositories.get(0);
                GitLocalBranch localBranch = repository.getCurrentBranch();
                if (localBranch == null) {
                    continue;
                }
                GitRemoteBranch remoteBranch = localBranch.findTrackedBranch(repository);
                if (remoteBranch == null) {
                    continue;
                }
                try {
                    VcsRevisionNumber localLastCommit = GitHistoryUtils.getCurrentRevision(repository.getProject(), VcsUtil.getFilePath(virtualFile), localBranch.getName());
                    VcsRevisionNumber remoteLastCommit = GitHistoryUtils.getCurrentRevision(repository.getProject(), VcsUtil.getFilePath(virtualFile), remoteBranch.getName());
                    if (localLastCommit == null || remoteLastCommit == null) {
                        continue;
                    }
                    if (!localLastCommit.asString().equals(remoteLastCommit.asString())) {
                        notification(PROJECT);
                        return;
                    }
                } catch (VcsException ignored) {
                }
            }
        }
    }

    private void notification(Project project) {
        Optional.ofNullable(notification).ifPresent(Notification::expire);
        AnAction anAction = new AnAction(Message.GO_GET_NEW_CODE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ToolWindowManager.getInstance(project).invokeLater(() -> {
                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.VCS);
                    ContentManager contentManager = toolWindow.getContentManager();
                    Optional.ofNullable(contentManager.findContent(VcsLogContentProvider.TAB_NAME)).ifPresent(contentManager::setSelectedContent);
                    toolWindow.show();
                });
            }
        };
        notification = Message.notifyInfo(project, Message.NEW_CODE_EXISTS, anAction);
    }
}
