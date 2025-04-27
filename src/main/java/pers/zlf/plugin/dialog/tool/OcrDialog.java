package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import pers.zlf.plugin.api.BaiDuApi;
import pers.zlf.plugin.api.BaseApi;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/14 9:23
 */
public class OcrDialog {
    private JTextArea downTextArea;
    private JButton downButton;
    private JTextField pdfFileNumTextField;
    private JPanel contentPanel;
    private TextFieldWithBrowseButton localFileField;
    private JTextField fileUrlTextField;
    private final Map<Integer, BaseApi> OCR_API_MAP = new HashMap<>() {{
        put(Common.BAIDU_OCR, new BaiDuApi());
    }};

    public OcrDialog() {
        localFileField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false, false, false, false)));
        Integer translateApi = ConfigFactory.getInstance().getCommonConfig().getTranslateApi();
        BaseApi baseApi = OCR_API_MAP.get(translateApi);
        downButton.addActionListener(e -> {
            try {
                List<String> ocrResult = baseApi.ocr(localFileField.getText(), fileUrlTextField.getText(), pdfFileNumTextField.getText());
                downTextArea.setText(String.join(System.lineSeparator(), ocrResult));
            } catch (Exception ex) {
                if (Message.PLEASE_CONFIGURE_OCR_FIRST.equals(ex.getMessage())) {
                    Message.notifyError(Message.PLEASE_CONFIGURE_OCR_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_ID_JAVA_CODE_HELPER);
                } else {
                    Message.notifyError(Message.OCR_FAILED + ex.getMessage());
                }
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
