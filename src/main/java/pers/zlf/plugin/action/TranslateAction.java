package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import pers.zlf.plugin.api.BaiDuTransApi;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.MESSAGE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/17 19:18
 */
public class TranslateAction extends BasicAction {
    private CommonConfig commonConfig;
    private String selectionText;

    @Override
    public boolean check() {
        if (null == editor) {
            return false;
        }
        //获取选择内容
        this.selectionText = editor.getSelectionModel().getSelectedText();
        if (StringUtil.isEmpty(selectionText)) {
            return false;
        }
        this.commonConfig = ConfigFactory.getInstance().getCommonConfig();
        if (StringUtil.isEmpty(commonConfig.getAppId()) || StringUtil.isEmpty(commonConfig.getSecretKey())) {
            Messages.showMessageDialog(MESSAGE.TRANSLATION_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon());
            return false;
        }
        return true;
    }

    @Override
    public void action() {
        ThreadPoolFactory.TRANS_POOL.execute(() -> {
            String from = COMMON.ZH;
            String to = COMMON.EN;
            if (StringUtil.isEnglish(selectionText)) {
                from = COMMON.EN;
                to = COMMON.ZH;
            }
            String translateResult = COMMON.BLANK_STRING;
            //请求翻译API
            try {
                if (COMMON.BAIDU_TRANSLATE.equals(commonConfig.getTranslateApi())) {
                    translateResult = new BaiDuTransApi().trans(commonConfig.getAppId(), commonConfig.getSecretKey(), selectionText, from, to);
                }
                Empty.of(translateResult).map(t -> COMMON.SPACE + t).ifPresent(t -> WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(editor.getSelectionModel().getSelectionEnd(), t)));
            } catch (Exception e) {
                String errorMessage = COMMON.TRANSLATE_MAP.get(commonConfig.getTranslateApi()) + COMMON.SPACE + COMMON.COLON + COMMON.SPACE + e.getMessage();
                WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(errorMessage, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }
}
