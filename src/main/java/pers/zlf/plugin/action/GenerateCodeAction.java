package pers.zlf.plugin.action;

import com.intellij.database.model.DasTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.constant.MyDataKeys;
import pers.zlf.plugin.dialog.GenerateCodeDialog;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.SwingUtil;

import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/19 18:13
 */
public class GenerateCodeAction extends BaseAction {
    /** 选中的表 */
    private DasTable selectDasTable;

    @Override
    protected boolean isVisible() {
        //获取选中的元素
        Object[] data = event.getData(MyDataKeys.DATABASE_NODES);
        if (data != null && data.length > 0 && data[0] instanceof DasTable dasTable) {
            selectDasTable = dasTable;
            return true;
        }
        return false;
    }

    @Override
    protected void execute() {
        //生成代码的窗口
        Map<String, Map<String, String>> totalTemplateMap = ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap();
        if (totalTemplateMap == null || totalTemplateMap.isEmpty()) {
            Message.notifyError(project, Message.PLEASE_CONFIGURE_TEMPLATE_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_ID_TEMPLATE);
            return;
        }
        SwingUtil.showToolWindow(project, Common.JAVA_CODE_HELPER, new GenerateCodeDialog(project, selectDasTable).getContent(), selectDasTable.getName() + Common.SPACE + Common.GENERATE_CODE);
    }

}
