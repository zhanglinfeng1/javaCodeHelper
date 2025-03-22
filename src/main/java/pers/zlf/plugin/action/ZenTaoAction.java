package pers.zlf.plugin.action;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.ZenTaoData;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.ZenTaoUtil;

import java.awt.Point;
import java.util.List;
import java.util.Optional;

import static com.intellij.util.ui.UI.scale;

/**
 * @author zhanglinfeng
 * @date create in 2025/3/20 10:21
 */
public class ZenTaoAction extends BaseAction {
    private String zenTaoUrl;
    private String zenTaoAccount;
    private String zenTaoPassword;
    private CommitMessage commitMessage;

    @Override
    protected boolean isVisible() {
        zenTaoUrl = ConfigFactory.getInstance().getCommonConfig().getZenTaoUrl();
        zenTaoAccount = ConfigFactory.getInstance().getCommonConfig().getZenTaoAccount();
        zenTaoPassword = ConfigFactory.getInstance().getCommonConfig().getZenTaoPassword();
        if (StringUtil.isEmpty(zenTaoUrl) || StringUtil.isEmpty(zenTaoAccount) || StringUtil.isEmpty(zenTaoPassword)) {
            return false;
        }
        CommitMessageI commitMessageI = event.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
        commitMessage = commitMessageI instanceof CommitMessage ? (CommitMessage) commitMessageI : null;
        return commitMessage != null;
    }

    @Override
    protected void execute() {
        List<String> valueList;
        try {
            ZenTaoData zenTaoData = ZenTaoUtil.getTaskAndBugList(zenTaoUrl, zenTaoAccount, zenTaoPassword);
            valueList = zenTaoData.getMessageList();
        } catch (Exception e) {
            Message.notifyError(project, e.getMessage());
            return;
        }
        JBPopupFactory.getInstance().createPopupChooserBuilder(valueList)
                .setItemChosenCallback(value -> {
                    String message = Optional.ofNullable(commitMessage).map(CommitMessage::getText).orElse(Common.BLANK_STRING);
                    commitMessage.setText(value + message + System.lineSeparator());
                }).addListener(new JBPopupListener() {
                    @Override
                    public void beforeShown(@NotNull LightweightWindowEvent event) {
                        JBPopup popup = event.asPopup();
                        RelativePoint relativePoint = new RelativePoint(commitMessage.getEditorField(), new Point(0, -scale(3)));
                        Point screenPoint = new Point(relativePoint.getScreenPoint());
                        screenPoint.translate(0, -popup.getSize().height);
                        popup.setLocation(screenPoint);
                    }
                }).createPopup().showInBestPositionFor(event.getDataContext());
    }
}
