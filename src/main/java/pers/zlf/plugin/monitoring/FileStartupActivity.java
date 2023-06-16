package pers.zlf.plugin.monitoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/12 10:17
 */
public class FileStartupActivity implements StartupActivity {
    private static Map<String, Integer> lineCountMap = new HashMap<>();

    @Override
    public void runActivity(@NotNull Project project) {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        boolean countComment = commonConfig.isCountComment();
        //TODO 监控文件变化 待优化
        VirtualFileManager.getInstance().addAsyncFileListener(events -> new AsyncFileListener.ChangeApplier() {
                    @Override
                    public void beforeVfsChange() {
                        AsyncFileListener.ChangeApplier.super.beforeVfsChange();
                        if (CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        BiFunction<VirtualFile, Integer, Integer> biFunction = (virtualFile, lineCount) -> {
                            lineCountMap.put(virtualFile.getPath(), lineCount);
                            return 0;
                        };
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile()) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileContentChangeEvent || vFileEvent instanceof VFileCopyEvent) {
                                updateLineCount(project, vFileEvent.getFile(), fileTypeList, countComment, biFunction);
                            }
                        }
                    }

                    @Override
                    public void afterVfsChange() {
                        AsyncFileListener.ChangeApplier.super.afterVfsChange();
                        if (CollectionUtil.isEmpty(fileTypeList)) {
                            return;
                        }
                        for (VFileEvent vFileEvent : events) {
                            if (null == vFileEvent.getFile() || vFileEvent instanceof VFileMoveEvent || vFileEvent instanceof VFilePropertyChangeEvent) {
                                continue;
                            }
                            if (vFileEvent instanceof VFileDeleteEvent) {
                                updateLineCount(project, vFileEvent.getFile(), fileTypeList, countComment, (virtualFile, lineCount) -> -lineCount);
                                continue;
                            }
                            if (vFileEvent instanceof VFileCreateEvent) {
                                updateLineCount(project, vFileEvent.getFile(), fileTypeList, countComment, (virtualFile, lineCount) -> lineCount);
                                continue;
                            }
                            Integer oldLineCount = lineCountMap.get(vFileEvent.getFile().getPath());
                            updateLineCount(project, vFileEvent.getFile(), fileTypeList, countComment, (virtualFile, lineCount) -> lineCount - oldLineCount);
                            lineCountMap.remove(vFileEvent.getFile().getPath());
                        }
                    }
                },
                () -> {
                });
    }

    private void updateLineCount(Project project, VirtualFile virtualFile, List<String> fileTypeList, boolean countComment, BiFunction<VirtualFile, Integer, Integer> biFunction) {
        boolean isProjectFile = Arrays.stream(ProjectRootManager.getInstance(project).getContentSourceRoots()).anyMatch(f -> virtualFile.getPath().startsWith(f.getPath()));
        if (!isProjectFile) {
            return;
        }
        //所属模块
        String moduleName = MyPsiUtil.getModuleNameByVirtualFile(virtualFile, project);
        if (StringUtil.isEmpty(moduleName) || !CodeLinesCountDecorator.lineCountMap.containsKey(moduleName)) {
            return;
        }
        int lineCount = MyPsiUtil.getLineCount(virtualFile, fileTypeList, countComment);
        int changeCount = biFunction.apply(virtualFile, lineCount);
        if (changeCount == 0) {
            return;
        }
        Optional.ofNullable(CodeLinesCountDecorator.lineCountMap.get(moduleName)).ifPresent(oldCount -> {
            Integer newCount = oldCount + changeCount;
            CodeLinesCountDecorator.lineCountMap.put(moduleName, newCount);
            // TODO 直接获取 PresentationData
            Optional.ofNullable(CodeLinesCountDecorator.presentationDataMap.get(moduleName)).ifPresent(data -> {
                String comment = data.getLocationString();
                if (null != comment && comment.contains(oldCount.toString())) {
                    data.setLocationString(comment.replaceFirst(String.format(REGEX.PARENTHESES_REPLACE, oldCount), COMMON.LEFT_PARENTHESES + newCount + COMMON.RIGHT_PARENTHESES));
                }
            });
        });
    }
}
