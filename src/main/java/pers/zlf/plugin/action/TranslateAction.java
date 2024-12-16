package pers.zlf.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import pers.zlf.plugin.api.BaiDuApi;
import pers.zlf.plugin.api.BaseApi;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.SwingUtil;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/17 19:18
 */
public class TranslateAction extends BaseAction {
    /** 选中的文本 */
    private String selectionText;
    /** 翻译API */
    private final Map<Integer, BaseApi> translateApiMap = new HashMap<>() {{
        put(Common.BAIDU_TRANSLATE, new BaiDuApi());
    }};

    @Override
    public boolean isVisible() {
        //获取选择内容
        this.selectionText = editor.getSelectionModel().getSelectedText();
        return null != editor && StringUtil.isNotEmpty(selectionText);
    }

    @Override
    public void execute() {
        Integer translateApi = ConfigFactory.getInstance().getCommonConfig().getTranslateApi();
        BaseApi baseApi = translateApiMap.get(translateApi);
        ThreadPoolFactory.TRANS_POOL.execute(() -> {
            //请求翻译API
            try {
                //翻译结果
                String translateResult = baseApi.trans(selectionText);
                if (StringUtil.isNotEmpty(translateResult)) {
                    //复制菜单
                    ActionListener actionListener = e -> {
                        if (e.getSource() instanceof MouseEvent mouseEvent) {
                            SwingUtil.createJBPopupMenu(mouseEvent, SwingUtil.MENU_ITEM_COPY);
                        }
                    };
                    //弹窗展示
                    JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
                    ApplicationManager.getApplication().invokeLater(() -> jbPopupFactory.createHtmlTextBalloonBuilder(translateResult, null, MessageType.INFO.getPopupBackground(), null)
                            .setTitle(baseApi.getTranslateApiName()).setClickHandler(actionListener, false).createBalloon().show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below));
                }
            } catch (Exception e) {
                if (Message.PLEASE_CONFIGURE_TRANSLATE_FIRST.equals(e.getMessage())) {
                    Message.notifyError(project, Message.PLEASE_CONFIGURE_TRANSLATE_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_JAVA_CODE_HELPER_ID);
                } else {
                    Message.notifyError(project, Message.TRANSLATE_FAILED + e.getMessage());
                }
            }
        });
    }

}
