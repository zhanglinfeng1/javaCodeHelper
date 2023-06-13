package pers.zlf.plugin.monitoring;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/12 10:17
 */
public class FileStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        boolean countComment = commonConfig.isCountComment();
        //监控文件变化
        PsiManager.getInstance(project).addPsiTreeChangeListener(
                new PsiTreeChangeListener() {
                    int oldLineCount = 0;

                    @Override
                    public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        Function<Integer, Integer> function = lineCount -> {
                            oldLineCount = lineCount;
                            return 0;
                        };
                        updateLineCount(project, psiTreeChangeEvent.getFile(), fileTypeList, countComment, function);
                    }

                    @Override
                    public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void childAdded(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        updateLineCount(project, psiTreeChangeEvent.getFile(), fileTypeList, countComment, lineCount -> lineCount);
                    }

                    @Override
                    public void childRemoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        updateLineCount(project, psiTreeChangeEvent.getFile(), fileTypeList, countComment, lineCount -> -lineCount);
                    }

                    @Override
                    public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                        updateLineCount(project, psiTreeChangeEvent.getFile(), fileTypeList, countComment, lineCount -> lineCount - oldLineCount);
                        oldLineCount = 0;
                    }

                    @Override
                    public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }

                    @Override
                    public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
                    }
                },
                () -> {
                });
    }

    private void updateLineCount(Project project, PsiFile psiFile, List<String> fileTypeList, boolean countComment, Function<Integer, Integer> function) {
        if (CollectionUtil.isEmpty(fileTypeList)) {
            return;
        }
        Optional.ofNullable(psiFile).map(PsiFile::getVirtualFile).ifPresent(virtualFile -> {
            //所属模块
            String moduleName = Optional.ofNullable(ModuleUtil.findModuleForFile(virtualFile, project)).map(Module::getName).orElse(COMMON.BLANK_STRING);
            Optional.ofNullable(CodeLinesCountDecorator.lineCountMap.get(moduleName)).ifPresent(data -> {
                //当前文件变化行数
                int changedLineCount = function.apply(MyPsiUtil.getLineCount(virtualFile, fileTypeList, countComment));
                if (changedLineCount == 0) {
                    return;
                }
                //更新行数
                String comment = StringUtil.toString(data.getLocationString());
                String oldTotalCount = StringUtil.getFirstMatcher(data.getLocationString(), REGEX.PARENTHESES);
                String newTotalCount = String.valueOf((Integer.parseInt(oldTotalCount) + changedLineCount));
                data.setLocationString(comment.replace(oldTotalCount, newTotalCount));
            });
        });
    }

}
