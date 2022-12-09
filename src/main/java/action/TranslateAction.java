package action;

import api.BaiDuTransApi;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import constant.COMMON;
import factory.ConfigFactory;
import pojo.CommonConfig;
import util.StringUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/17 19:18
 */
public class TranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        //获取当前的编辑器对象
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            return;
        }
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (null == project) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        //获取选择内容
        String selectedText = selectionModel.getSelectedText();
        if (StringUtil.isEmpty(selectedText)) {
            return;
        }
        //获取翻译配置
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        String appid = commonConfig.getAppId();
        String securityKey = commonConfig.getSecretKey();
        if (StringUtil.isEmpty(appid) || StringUtil.isEmpty(securityKey)) {
            Messages.showMessageDialog("Please configure first! File > Setting > Other Settings > JavaCodeHelp", COMMON.BLANK_STRING, Messages.getInformationIcon());
        }
        new Thread(() -> {
            String from = COMMON.ZH;
            String to = COMMON.EN;
            if (StringUtil.isEnglish(selectedText)) {
                from = COMMON.EN;
                to = COMMON.ZH;
            }
            String translateResult = COMMON.BLANK_STRING;
            //请求翻译API
            try {
                if (COMMON.BAIDU_TRANSLATE.equals(commonConfig.getApiType())) {
                    translateResult = new BaiDuTransApi().trans(appid, securityKey, selectedText, from, to);
                }
            } catch (Exception e) {
                Messages.showMessageDialog(e.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
            }
            if (StringUtil.isEmpty(translateResult)) {
                return;
            }
            String finalSelectedText = selectedText + COMMON.SPACE + translateResult;
            WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), finalSelectedText));
        }).start();
    }
}
