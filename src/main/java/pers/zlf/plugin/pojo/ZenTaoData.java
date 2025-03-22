package pers.zlf.plugin.pojo;

import pers.zlf.plugin.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/19 23:06
 */
public class ZenTaoData {
    /** bug列表 */
    private List<ZenTaoDataDetail> bugs;
    /** 任务列表 */
    private List<ZenTaoDataDetail> tasks;

    public List<ZenTaoDataDetail> getBugs() {
        return bugs;
    }

    public void setBugs(List<ZenTaoDataDetail> bugs) {
        this.bugs = bugs;
    }

    public List<ZenTaoDataDetail> getTasks() {
        return tasks;
    }

    public void setTasks(List<ZenTaoDataDetail> tasks) {
        this.tasks = tasks;
    }

    public List<String> getMessageList() {
        List<String> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bugs)) {
            bugs.forEach(t -> list.add("BUG：" + t.getId() + " " + t.getTitle()));
        }
        if (CollectionUtil.isNotEmpty(tasks)) {
            tasks.forEach(t -> list.add("TASK：" + t.getId() + " " + t.getName()));
        }
        return list;
    }
}
