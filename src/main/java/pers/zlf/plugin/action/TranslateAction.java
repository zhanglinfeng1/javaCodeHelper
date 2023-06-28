package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import pers.zlf.plugin.api.BaiDuTransApi;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/17 19:18
 */
public class TranslateAction extends BaseAction {
    private CommonConfig commonConfig;
    private String selectionText;

    @Override
    public boolean check() {
        if (null == editor || !psiFile.isWritable()) {
            return false;
        }
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
    public void action() {
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
                Empty.of(translateResult).map(t -> Common.SPACE + t).ifPresent(t -> WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(editor.getSelectionModel().getSelectionEnd(), t)));
            } catch (Exception e) {
                WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(e.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }
}
