package action;

import api.BaiDuTransApi;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import component.ConfigComponent;
import constant.COMMON_CONSTANT;
import pojo.Config;
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
        String selectedText = selectionModel.getSelectedText();
        if (StringUtil.isEmpty(selectedText)) {
            return;
        }
        //获取配置
        Config config = ServiceManager.getService(ConfigComponent.class).getState();
        String appid = config.getAppId();
        String securityKey = config.getSecretKey();
        if (StringUtil.isEmpty(appid) || StringUtil.isEmpty(securityKey)) {
            Messages.showMessageDialog("请先配置 File > Setting > Other Settings > JavaCodeHelp", COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
        }
        //获取选择内容
        selectedText = selectedText.replaceAll(COMMON_CONSTANT.WRAP_REGEX, COMMON_CONSTANT.BLANK_STRING).trim();
        String translateResult = COMMON_CONSTANT.BLANK_STRING;
        String from = COMMON_CONSTANT.EN;
        String to = COMMON_CONSTANT.ZH;
        if (!StringUtil.isEnglish(selectedText)) {
            from = COMMON_CONSTANT.ZH;
            to = COMMON_CONSTANT.EN;
        }
        if (COMMON_CONSTANT.BAIDU_TRANSLATE.equals(config.getApi())) {
            translateResult = new BaiDuTransApi().trans(appid, securityKey, selectedText, from, to);
        }
        String finalSelectedText = selectedText + translateResult;
        WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), finalSelectedText));
    }

}
