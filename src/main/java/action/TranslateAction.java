package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import constant.COMMON_CONSTANT;
import util.JsonUtil;
import util.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        selectedText = selectedText.replaceAll("[\r\n/*]", "").trim();
        String translateResult;
        if (StringUtil.isEnglish(selectedText)) {
            translateResult = translate(selectedText, COMMON_CONSTANT.EN, COMMON_CONSTANT.ZH_CN);
        } else {
            translateResult = translate(selectedText, COMMON_CONSTANT.ZH_CN, COMMON_CONSTANT.EN);
        }
        String finalSelectedText = selectedText;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), finalSelectedText + translateResult);
        });
    }

    private String translate(String text, String sourceLang, String targetLang) {
        StringBuilder responseStr = new StringBuilder(COMMON_CONSTANT.BLANK_STRING);
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            return responseStr.toString();
        }
        // 将jsonArray转java对象list
        List<Object> objList = JsonUtil.toList(responseStr.toString(),Object.class);
        List<Object> objList2 = JsonUtil.toList(objList.get(0).toString(),Object.class);
        List<Object> objList3 = JsonUtil.toList(objList2.get(0).toString(),Object.class);
        return objList3.get(0).toString();
    }
}
