package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.api.BaiDuTransApi;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/17 19:18
 */
public class TranslateAction extends BasicAction<PsiElement> {
    private CommonConfig commonConfig;
    private String selectionText;
    private int selectionEnd;

    @Override
    public boolean check() {
        //获取选择内容
        this.selectionText = editor.getSelectionModel().getSelectedText();
        if (StringUtil.isEmpty(selectionText)) {
            return false;
        }
        this.selectionEnd = editor.getSelectionModel().getSelectionEnd();
        this.commonConfig = ConfigFactory.getInstance().getCommonConfig();
        if (StringUtil.isEmpty(commonConfig.getAppId()) || StringUtil.isEmpty(commonConfig.getSecretKey())) {
            Messages.showMessageDialog("Please configure first! File > Setting > Other Settings > JavaCodeHelp", COMMON.BLANK_STRING, Messages.getInformationIcon());
            return false;
        }
        return true;
    }

    @Override
    public void action(PsiElement element) {
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
                if (COMMON.BAIDU_TRANSLATE.equals(commonConfig.getApiType())) {
                    translateResult = new BaiDuTransApi().trans(commonConfig.getAppId(), commonConfig.getSecretKey(), selectionText, from, to);
                }
                Empty.of(translateResult).map(t -> COMMON.SPACE + t).isPresent(t -> WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(selectionEnd, t)));
            } catch (Exception e) {
                String errorMessage = COMMON.TRANSLATE_MAP.get(commonConfig.getApiType()) + COMMON.SPACE + COMMON.COLON + COMMON.SPACE + e.getMessage();
                WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(errorMessage, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            }
        });
    }
}
