package pers.zlf.plugin.action;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.CodeCountUtil;
import pers.zlf.plugin.util.CollectionUtil;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BaseAction {

    @Override
    protected boolean isVisible() {
        return null != project;
    }

    @Override
    protected boolean isExecute() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCodeStatisticsConfig().getFileTypeList())) {
            Message.notifyError(project, Message.PLEASE_CONFIGURE_FILE_TYPE_LIST_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_ID_CODE_STATISTICS);
            return false;
        }
        return true;
    }

    @Override
    protected void execute() {
        CodeCountUtil.countCodeLines(project);
    }

}
