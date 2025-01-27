package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.util.lambda.Equals;

/**
 * @author zhanglinfeng
 * @date create in 2022/12/28 9:54
 */
public abstract class BaseAction extends AnAction {
    protected AnActionEvent event;
    protected Editor editor;
    protected Project project;
    protected Module module;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Equals.of(isExecute()).ifTrue(this::execute);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        //获取当前的编辑器对象
        this.event = event;
        this.editor = event.getData(PlatformDataKeys.EDITOR);
        this.project = event.getData(CommonDataKeys.PROJECT);
        this.module = event.getData(PlatformCoreDataKeys.MODULE);
        event.getPresentation().setVisible(isVisible());
    }

    @NotNull
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * 校验是否显示
     *
     * @return boolean
     */
    protected boolean isVisible() {
        return true;
    }

    /**
     * 校验是否执行 execute方法
     *
     * @return boolean
     */
    protected boolean isExecute() {
        return true;
    }

    /**
     * 具体执行内容
     */
    protected abstract void execute();

}
