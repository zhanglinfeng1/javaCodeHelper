package pers.zlf.plugin.monitoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.action.CodeLineCountAction;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.PathUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/12 10:17
 */
public class FileStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        CodeStatisticsConfig config = ConfigFactory.getInstance().getCodeStatisticsConfig();
        List<String> fileTypeList = config.getFileTypeList();
        VirtualFileManager.getInstance().addAsyncFileListener(events -> new AsyncFileListener.ChangeApplier() {
                    private final Map<String, Integer> fileLineCountMap = new HashMap<>(2);

                    @Override
                    public void beforeVfsChange() {
                        AsyncFileListener.ChangeApplier.super.beforeVfsChange();
                        if (!config.isRealTimeStatistics() || CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        fileLineCountMap.clear();
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile() || vFileEvent.getFile().isDirectory()) {
                                continue;
                            }
                            if (PathUtil.contain(vFileEvent.getFile().getPath(), Common.DOT_IDEA)) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileContentChangeEvent) {
                                fileLineCountMap.put(vFileEvent.getFile().getPath(), CodeLineCountAction.getLineCount(vFileEvent.getFile()));
                            }
                        }
                    }

                    @Override
                    public void afterVfsChange() {
                        AsyncFileListener.ChangeApplier.super.afterVfsChange();
                        if (!config.isRealTimeStatistics() || CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile() || vFileEvent.getFile().isDirectory()) {
                                continue;
                            }
                            if (PathUtil.contain(vFileEvent.getFile().getPath(), Common.DOT_IDEA)) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileDeleteEvent) {
                                CodeLinesCountDecorator.updateLineCount(project, vFileEvent.getFile(), -CodeLineCountAction.getLineCount(vFileEvent.getFile()));
                            } else if (vFileEvent instanceof VFileCopyEvent) {
                                CodeLinesCountDecorator.updateLineCount(project, vFileEvent.getFile(), CodeLineCountAction.getLineCount(vFileEvent.getFile()));
                            } else if (vFileEvent instanceof VFileContentChangeEvent) {
                                Integer oldCount = fileLineCountMap.get(vFileEvent.getFile().getPath());
                                if (null == oldCount || oldCount <= 0) {
                                    continue;
                                }
                                int changeCount = CodeLineCountAction.getLineCount(vFileEvent.getFile()) - oldCount;
                                CodeLinesCountDecorator.updateLineCount(project, vFileEvent.getFile(), changeCount);
                            }
                        }
                    }
                },
                () -> {
                });

    }
}
