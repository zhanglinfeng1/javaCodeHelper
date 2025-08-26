package pers.zlf.plugin.constant;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * 弹窗提示文本
 *
 * @author zhanglinfeng
 * @date create in 2023/5/10 10:13
 */
public class Message {
    /** 代码检查 */
    public static final String UNUSED_METHOD = "(javaCodeHelper) Method %s is never used";
    public static final String SUGGESTED_USE = "(javaCodeHelper) 推荐使用";
    public static final String OPTIONAL = "(javaCodeHelper) 推荐使用 Optional.ofNullable()";
    public static final String OPTIONAL_FIX_NAME = "Replace with Optional.ofNullable()";

    /** 配置 */
    public static final String TO_CONFIGURE = "去配置";
    public static final String PLEASE_CONFIGURE_AUTHOR_FIRST = "请先配置作者";
    public static final String PLEASE_CONFIGURE_TEMPLATE_FIRST = "请先配置模版";
    public static final String PLEASE_CONFIGURE_FILE_TYPE_LIST_FIRST = "请先配置参与统计的文件";
    public static final String PLEASE_CONFIGURE_TRANSLATE_API_FIRST = "请先配置翻译api";
    public static final String DATE_FORMAT_ERROR = "日期格式错误";
    public static final String CANNOT_BE_ZERO = "不能为0";
    public static final String PLEASE_CONFIGURE_OCR_FIRST = "请先配置文字识别";

    /** 代码统计 */
    public static final String STATISTICS_IN_PROGRESS = "正在统计中...";

    /** 模版生成代码 */
    public static final String FULL_PATH_CREATE_ERROR = "文件全路径创建失败";
    public static final String CREATE_FILE_ERROR = "文件创建失败";
    public static final String FULL_PATH_NOT_NULL = "文件全路径不能为空";
    public static final String PACKAGE_PATH_NOT_NULL = "包路径不能为空";
    public static final String GENERATE_CODE_SUCCESS = "代码生成成功，请手动移动文件至合理目录";
    public static final String DELETE_TEMPLATE = "确认删除模版 '%s' 吗？";
    public static final String DELETE_TEMPLATE_FILE = "确认删除模版文件 '%s' 吗？";
    public static final String TEMPLATE_EXISTING = "已存在同名模版";
    public static final String TEMPLATE_FILE_EXISTING = "已存在同名模版文件";
    public static final String UPDATE_TEMPLATE = "模版 '%s' 有改动，是否保存？";
    public static final String TEMPLATE_NAME = "模版名称";
    public static final String TEMPLATE_FILE_NAME = "模版文件名称";
    public static final String UPDATE_TEMPLATE_NAME = "修改模版名称";
    public static final String RESET_TEMPLATE = "将当前模版的所有文件重置为默认的模版文件";
    public static final String GENERATE_CODE_FAILED = "代码生成失败：";

    /** 工具 */
    public static final String NOT_CRON_EXPRESSIONS = "非cron表达式";
    public static final String AES_SECRET_KEY_LENGTH_ERROR = "秘钥长度必须为16位";
    public static final String AES_IV_LENGTH_ERROR = "偏移量长度必须为16位";
    public static final String FORMAT_ERROR = "格式错误";
    public static final String ENCRYPT_FAILED = "加密失败：";
    public static final String DECRYPT_FAILED = "解密失败：";
    public static final String TRANSLATE_FAILED = "翻译失败：";
    public static final String EXPORT_CONTRIBUTION_DETAILS_FAILED = "导出贡献详情失败：";
    public static final String EXPORT_SUCCESS = "导出成功，文件目录：";
    public static final String EXPORT_DATABASE_TABLE_STRUCTURE_FAILED = "导出数据库表结构失败：";
    public static final String GENERATE_QR_CODE_FIRST = "请先生成二维码";
    public static final String UPLOAD_FIRST = "请先上传";
    public static final String GENERATE_QR_CODE_FAILED = "生成二维码失败：";
    public static final String DOWNLOAD_FAILED = "保存失败：";
    public static final String ANALYSIS_QR_CODE_FAILED = "解析二维码失败：";
    public static final String UPLOAD_FAILED = "上传失败：";
    public static final String NEW_CODE_EXISTS = "当前分支存在新代码，请及时拉取";
    public static final String GO_GET_NEW_CODE = "去拉取";
    public static final String CLOSE = "Close";
    public static final String OCR_PDF_FILE_NUM_EMPTY = "待识别的pdf页面码不能为空";
    public static final String OCR_FAILED = "识别失败：";
    public static final String OCR_FILE_EMPTY = "待识别文件不能为空";
    public static final String ZENTAO_REQUEST_SESSION_FAILED = "获取禅道session失败：";
    public static final String ZENTAO_REQUEST_BUG_FAILED = "获取禅道bug失败：";
    public static final String ZENTAO_REQUEST_TASK_FAILED = "获取禅道任务失败：";
    public static final String ZENTAO_LOGIN_FAILED = "禅道登录失败：";
    public static final String PIC_TO_BASE64_FAILED = "图片转Base64失败：";
    public static final String PIC_TO_PDF_FAILED = "图片转PDF失败：";
    public static final String PIC_TO_PDF_SUCCESS = "图片转PDF成功，文件目录：";
    public static final String FILE_SUFFIX_NOT_NULL = "图片后缀不能为空";
    public static final String BASE64_TO_PIC_FAILED = "Base64转图片失败：";
    public static final String BASE64_TO_PIC_FIRST = "请先Base64转图片";

    /** 通知组ID */
    public static final String NOTIFICATION_GROUP_ID = "JavaCodeHelper";

    /**
     * info消息
     *
     * @param content 消息文本
     */
    public static void notifyInfo(String content) {
        MyPsiUtil.getCurrentProject(project -> notifyInfo(project, content));
    }

    /**
     * info消息
     *
     * @param content     消息文本
     * @param delayMillis 指定时间后自动关闭
     */
    public static void notifyInfo(String content, Integer delayMillis) {
        MyPsiUtil.getCurrentProject(project -> notify(project, content, NotificationType.INFORMATION, null, delayMillis));
    }

    /**
     * info消息
     *
     * @param project 消息展示的项目
     * @param content 消息文本
     */
    public static void notifyInfo(@NotNull Project project, String content) {
        notifyInfo(project, content, null);
    }

    /**
     * info消息
     *
     * @param project 消息展示的项目
     * @param content 消息文本
     * @param action  执行动作
     */
    public static Notification notifyInfo(@NotNull Project project, String content, AnAction action) {
        return notify(project, content, NotificationType.INFORMATION, action, null);
    }

    /**
     * error消息
     *
     * @param content 消息文本
     */
    public static void notifyError(String content) {
        MyPsiUtil.getCurrentProject(project -> notifyError(project, content, null, null));
    }

    /**
     * error消息
     *
     * @param project 消息展示的项目
     * @param content 消息文本
     */
    public static void notifyError(@NotNull Project project, String content) {
        notifyError(project, content, null, null);
    }

    /**
     * error消息
     *
     * @param content                   消息文本
     * @param linkText                  链接id
     * @param applicationConfigurableId 配置页id
     */
    public static void notifyError(String content, String linkText, String applicationConfigurableId) {
        MyPsiUtil.getCurrentProject(project -> notifyError(project, content, linkText, applicationConfigurableId));
    }

    /**
     * error消息
     *
     * @param project                   消息展示的项目
     * @param content                   消息文本
     * @param linkText                  链接id
     * @param applicationConfigurableId 配置页id
     */
    public static void notifyError(@NotNull Project project, String content, String linkText, String applicationConfigurableId) {
        AnAction anAction = null;
        if (StringUtil.isNotEmpty(linkText)) {
            anAction = new AnAction(linkText) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    ShowSettingsUtilImpl.showSettingsDialog(project, applicationConfigurableId, Common.BLANK_STRING);
                }
            };
        }
        notify(project, content, NotificationType.ERROR, anAction, null);
    }

    /**
     * 通知
     *
     * @param project          消息展示的项目
     * @param content          消息文本
     * @param notificationType 消息类型
     * @param action           执行动作
     * @param delayMillis      指定时间后自动关闭
     */
    private static Notification notify(@NotNull Project project, String content, NotificationType notificationType, AnAction action, Integer delayMillis) {
        Notification notification = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID).createNotification(Common.JAVA_CODE_HELPER, content, notificationType).setIcon(MyIcon.LOGO).setSuggestionType(true);
        Optional.ofNullable(action).ifPresent(notification::addAction);
        if (delayMillis == null) {
            notification.addAction(new AnAction(Message.CLOSE) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    notification.expire();
                }
            });
        } else {
            new Alarm(Alarm.ThreadToUse.SWING_THREAD).addRequest(notification::expire, delayMillis);
        }
        notification.notify(project);
        return notification;
    }

}
