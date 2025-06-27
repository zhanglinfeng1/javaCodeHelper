package pers.zlf.plugin.schedule;

import com.intellij.openapi.project.Project;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.ZenTaoData;
import pers.zlf.plugin.pojo.ZenTaoDataDetail;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.ZenTaoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/27 15:31
 */
public class ZenTaoRemind implements Runnable {
    private final Project PROJECT;
    private boolean first = true;
    private List<String> bugIdList = new ArrayList<>();
    private List<String> tasksIdList = new ArrayList<>();

    public ZenTaoRemind(Project project) {
        this.PROJECT = project;
    }

    @Override
    public void run() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        String zenTaoUrl = commonConfig.getZenTaoUrl();
        String zenTaoAccount = commonConfig.getZenTaoAccount();
        String zenTaoPassword = commonConfig.getZenTaoPassword();
        if (StringUtil.isEmpty(zenTaoUrl) || StringUtil.isEmpty(zenTaoAccount) || StringUtil.isEmpty(zenTaoPassword)) {
            return;
        }
        try {
            ZenTaoData zenTaoData = ZenTaoUtil.getTaskAndBugList(commonConfig.getZenTaoUrl(), commonConfig.getZenTaoAccount(), commonConfig.getZenTaoPassword());
            List<String> messageList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(zenTaoData.getBugs())) {
                for (ZenTaoDataDetail detail : zenTaoData.getBugs()) {
                    if (detail.needToDo() && !bugIdList.contains(detail.getId())) {
                        messageList.add(detail.getBugMessage());
                    }
                }
                bugIdList = zenTaoData.getBugs().stream().map(ZenTaoDataDetail::getId).toList();
            }
            if (CollectionUtil.isNotEmpty(zenTaoData.getTasks())) {
                for (ZenTaoDataDetail detail : zenTaoData.getTasks()) {
                    if (detail.needToDo() && !tasksIdList.contains(detail.getId())) {
                        messageList.add(detail.getTaskMessage());
                    }
                }
                tasksIdList = zenTaoData.getBugs().stream().map(ZenTaoDataDetail::getId).toList();
            }
            //首次不提醒
            if (first) {
                first = false;
                return;
            }
            if (CollectionUtil.isNotEmpty(messageList)) {
                Message.notifyInfo(PROJECT, String.join(Common.LINE_BREAK_HTML, messageList), null);
            }
        } catch (Exception e) {
            Message.notifyError(PROJECT, e.getMessage());
        }
    }

}
