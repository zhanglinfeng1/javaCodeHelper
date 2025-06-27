package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.Common;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/19 23:06
 */
public class ZenTaoDataDetail {
    /** id */
    private String id;
    /** 标bug题 */
    private String title;
    /** 任务名 */
    private String name;
    /** 状态 */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean needToDo() {
        return "wait".equals(this.status) || "active".equals(this.status);
    }

    public String getBugMessage() {
        return "BUG：" + this.id + Common.SPACE + this.title;
    }

    public String getTaskMessage() {
        return "TASK：" + this.id + Common.SPACE + this.name;
    }
}
