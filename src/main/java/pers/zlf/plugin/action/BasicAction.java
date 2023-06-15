package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import pers.zlf.plugin.util.lambda.Equals;

public abstract class BasicAction extends AnAction {
    public AnActionEvent event;
    public Editor editor;
    public Project project;
    public PsiFile psiFile;

    @Override
    public void actionPerformed(AnActionEvent event) {
        this.event = event;
        //获取当前的编辑器对象
        this.editor = event.getData(PlatformDataKeys.EDITOR);
        this.project = event.getData(CommonDataKeys.PROJECT);
        this.psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Equals.of(null != project && check()).ifTrue(this::action);
    }

    public abstract boolean check();

    public abstract void action();

}
