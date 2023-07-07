package pers.zlf.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
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
        ThreadPoolFactory.TRANS_POOL.execute(() -> {
            //请求翻译API
            try {
                BaseApi baseApi;
                if (Common.BAIDU_TRANSLATE.equals(getTranslateApi)) {
                    baseApi = new BaiDuApi();
                } else {
                    return;
                }
                String translateResult = baseApi.trans(selectionText);
                if (StringUtil.isNotEmpty(translateResult)) {
                    String title = Common.TRANSLATE_MAP.get(ConfigFactory.getInstance().getCommonConfig().getTranslateApi());
                    JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
                    ApplicationManager.getApplication().invokeLater(() -> jbPopupFactory.createHtmlTextBalloonBuilder(translateResult, null, MessageType.INFO.getPopupBackground(), null)
                            .setTitle(title).createBalloon().show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below));
                }
            } catch (Exception e) {
                WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }
}
