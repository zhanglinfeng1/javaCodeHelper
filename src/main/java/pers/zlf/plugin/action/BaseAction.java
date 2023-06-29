package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.util.lambda.Equals;

/**
 * @author zhanglinfeng
 * @date create in 2022/12/28 9:54
 */
public abstract class BaseAction extends AnAction {
    protected Editor editor;
    protected Project project;
    protected PsiFile psiFile;
    protected VirtualFile virtualFile;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        //获取当前的编辑器对象
        init(event);
        Equals.of(null != project && check()).ifTrue(this::action);
    }

    protected void init(AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);
        this.project = event.getData(CommonDataKeys.PROJECT);
        this.virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
        this.psiFile = event.getData(CommonDataKeys.PSI_FILE);
    }

    /**
     * 校验是否执行action
     *
     * @return boolean
     */
    public boolean check() {
        return true;
    }

    /**
     * 具体执行内容
     */
    public abstract void action();

}
