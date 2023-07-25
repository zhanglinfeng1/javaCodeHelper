package pers.zlf.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import pers.zlf.plugin.api.BaiDuApi;
import pers.zlf.plugin.api.BaseApi;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.SwingUtil;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/17 19:18
 */
public class TranslateAction extends BaseAction {
    private String selectionText;

    @Override
    public boolean isVisible() {
        //获取选择内容
        this.selectionText = editor.getSelectionModel().getSelectedText();
        return null != editor && StringUtil.isNotEmpty(selectionText);
    }

    @Override
    public void execute() {
        Integer getTranslateApi = ConfigFactory.getInstance().getCommonConfig().getTranslateApi();
        BaseApi baseApi;
        if (Common.BAIDU_TRANSLATE.equals(getTranslateApi)) {
            baseApi = new BaiDuApi();
        } else {
            return;
        }
        ThreadPoolFactory.TRANS_POOL.execute(() -> {
            //请求翻译API
            try {
                //翻译结果
                String translateResult = baseApi.trans(selectionText);
                if (StringUtil.isNotEmpty(translateResult)) {
                    //复制菜单
                    ActionListener actionListener = e -> {
                        if (e.getSource() instanceof MouseEvent) {
                            MouseEvent mouseEvent = (MouseEvent) e.getSource();
                            SwingUtil.createJBPopupMenu(mouseEvent, Common.MENU_ITEM_COPY);
                        }
                    };
                    //弹窗展示
                    JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
                    ApplicationManager.getApplication().invokeLater(() -> jbPopupFactory.createHtmlTextBalloonBuilder(translateResult, null, MessageType.INFO.getPopupBackground(), null)
                            .setTitle(baseApi.getApiName()).setClickHandler(actionListener, false).createBalloon().show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below));
                }
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }

}
