package pers.zlf.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.util.ui.JBUI;
import pers.zlf.plugin.api.BaiDuTransApi;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/17 19:18
 */
public class TranslateAction extends BaseAction {
    private CommonConfig commonConfig;
    private String selectionText;

    @Override
    public boolean isVisible() {
        return null != editor;
    }

    @Override
    public boolean isExecute() {
        //获取选择内容
        this.selectionText = editor.getSelectionModel().getSelectedText();
        if (StringUtil.isEmpty(selectionText)) {
            return false;
        }
        this.commonConfig = ConfigFactory.getInstance().getCommonConfig();
        if (StringUtil.isEmpty(commonConfig.getAppId()) || StringUtil.isEmpty(commonConfig.getSecretKey())) {
            Messages.showMessageDialog(Message.TRANSLATION_CONFIGURATION, Common.BLANK_STRING, Messages.getInformationIcon());
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        ThreadPoolFactory.TRANS_POOL.execute(() -> {
            String from = Common.ZH;
            String to = Common.EN;
            if (StringUtil.isEnglish(selectionText)) {
                from = Common.EN;
                to = Common.ZH;
            }
            String translateResult = Common.BLANK_STRING;
            //请求翻译API
            try {
                if (Common.BAIDU_TRANSLATE.equals(commonConfig.getTranslateApi())) {
                    translateResult = new BaiDuTransApi().trans(commonConfig.getAppId(), commonConfig.getSecretKey(), selectionText, from, to);
                }
                if (StringUtil.isNotEmpty(translateResult)) {
                    String title = Common.TRANSLATE_MAP.get(commonConfig.getTranslateApi());
                    JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();
                    String finalTranslateResult = translateResult;
                    ApplicationManager.getApplication().invokeLater(() -> jbPopupFactory.createHtmlTextBalloonBuilder(finalTranslateResult, null, JBUI.CurrentTheme.NotificationInfo.backgroundColor(), null)
                            .setTitle(title).createBalloon().show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below));
                }
            } catch (Exception e) {
                WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }
}
