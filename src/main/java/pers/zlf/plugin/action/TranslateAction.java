package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.api.BaiDuTransApi;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.ThreadPoolFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/17 19:18
 */
public class TranslateAction extends BasicAction<PsiElement> {
    private CommonConfig commonConfig;
    private SelectionModel selectionModel;

    @Override
    public boolean check() {
        this.selectionModel = editor.getSelectionModel();
        //获取选择内容
        String selectedText = selectionModel.getSelectedText();
        if (StringUtil.isEmpty(selectedText)) {
            return false;
        }
        this.commonConfig = ConfigFactory.getInstance().getCommonConfig();
        String appid = commonConfig.getAppId();
        String securityKey = commonConfig.getSecretKey();
        if (StringUtil.isEmpty(appid) || StringUtil.isEmpty(securityKey)) {
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
            if (StringUtil.isEnglish(selectionModel.getSelectedText())) {
                from = COMMON.EN;
                to = COMMON.ZH;
            }
            String translateResult = COMMON.BLANK_STRING;
            //请求翻译API
            if (COMMON.BAIDU_TRANSLATE.equals(commonConfig.getApiType())) {
                translateResult = new BaiDuTransApi().trans(commonConfig.getAppId(), commonConfig.getSecretKey(), selectionModel.getSelectedText(), from, to);
            }
            if (StringUtil.isNotEmpty(translateResult)) {
                String finalSelectedText = COMMON.SPACE + translateResult;
                WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(selectionModel.getSelectionEnd(), finalSelectedText));
            }
        });
    }
}
