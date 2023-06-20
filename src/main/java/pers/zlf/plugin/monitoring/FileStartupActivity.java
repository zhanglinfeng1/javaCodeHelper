package pers.zlf.plugin.monitoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.action.CodeLineCountAction;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/12 10:17
 */
public class FileStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        //监听文件变化
        VirtualFileManager.getInstance().addAsyncFileListener(events -> new AsyncFileListener.ChangeApplier() {
                    private Map<String, Integer> fileLineCountMap = new HashMap<>();

                    @Override
                    public void beforeVfsChange() {
                        AsyncFileListener.ChangeApplier.super.afterVfsChange();
                        if (!commonConfig.isRealTimeStatistics() || CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        fileLineCountMap.clear();
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile() || vFileEvent.getFile().isDirectory()) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileMoveEvent || vFileEvent instanceof VFileCopyEvent || vFileEvent instanceof VFileContentChangeEvent) {
                                fileLineCountMap.put(vFileEvent.getFile().getPath(), CodeLineCountAction.getLineCount(vFileEvent.getFile()));
                            }
                        }
                    }

                    @Override
                    public void afterVfsChange() {
                        AsyncFileListener.ChangeApplier.super.afterVfsChange();
                        if (!commonConfig.isRealTimeStatistics() || CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile() || vFileEvent.getFile().isDirectory()) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileDeleteEvent) {
                                CodeLinesCountDecorator.updateLineCount(project, vFileEvent.getFile(), CodeLineCountAction.getLineCount(vFileEvent.getFile()));
                            }
                            if (vFileEvent instanceof VFileMoveEvent || vFileEvent instanceof VFileCopyEvent || vFileEvent instanceof VFileContentChangeEvent) {
                                Integer oldCount = fileLineCountMap.get(vFileEvent.getFile().getPath());
                                if (null == oldCount) {
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
