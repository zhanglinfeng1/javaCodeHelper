package pers.zlf.plugin.schedule;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
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
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.GitUtil;
import git4idea.fetch.GitFetchSupport;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;

import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 22:51
 */
public class NewCodeRemind implements Runnable {
    private final Project project;

    public NewCodeRemind(Project project) {
        this.project = project;
    }

    @Override
    public void run() {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        List<GitRepository> repositories = repositoryManager.getRepositories();
        //执行fetch
        GitFetchSupport.fetchSupport(project).fetchAllRemotes(repositories);
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
                GitRepository repository = repositories.get(0);
                GitLocalBranch localBranch = repository.getCurrentBranch();
                if (localBranch == null) {
                    continue;
                }
                GitRemoteBranch remoteBranch = localBranch.findTrackedBranch(repository);
                try {
                    //TODO 替换getFilePath方法
                    VcsRevisionNumber localLastCommit = GitHistoryUtils.getCurrentRevision(repository.getProject(), VcsUtil.getFilePath(virtualFile.getPath()), localBranch.getName());
                    VcsRevisionNumber remoteLastCommit = GitHistoryUtils.getCurrentRevision(repository.getProject(), VcsUtil.getFilePath(virtualFile.getPath()), remoteBranch.getName());
                    if (localLastCommit == null || remoteLastCommit == null) {
                        continue;
                    }
                    if (!localLastCommit.asString().equals(remoteLastCommit.asString())) {
                        notification(project);
                        return;
                    }
                } catch (VcsException ignored) {
                }
            }
        }

    }

    private void notification(Project project) {
        AnAction anAction = new NotificationAction(Message.GO_GET_NEW_CODE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                ToolWindowManager.getInstance(project).invokeLater(() -> {
                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.VCS);
                    ContentManager contentManager = toolWindow.getContentManager();
                    Optional.ofNullable(contentManager.findContent(Common.TOOL_WINDOW_PANEL_ID_LOG)).ifPresent(contentManager::setSelectedContent);
                    toolWindow.show();
                });
            }
        };
        Message.notifyInfo(project, Message.NEW_CODE_EXISTS, anAction);
    }
}
