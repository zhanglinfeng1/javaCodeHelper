package pers.zlf.plugin.schedule;

import com.intellij.openapi.project.Project;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/20 22:51
 */
public abstract class Schedule implements Runnable {
    protected final Project PROJECT;

    public Schedule(Project project) {
        this.PROJECT = project;
    }

}
