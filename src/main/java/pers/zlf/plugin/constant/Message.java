package pers.zlf.plugin.constant;

/**
 * 弹窗提示文本
 *
 * @author zhanglinfeng
 * @date create in 2023/5/10 10:13
 */
public class Message {
    /** 代码检查 */
    public static final String UNUSED_METHOD = "(JavaCodeHelp) Method %s is never used";
    public static final String SUGGESTED_USE = "(JavaCodeHelp) 推荐使用";
    public static final String OPTIONAL = "(JavaCodeHelp) 推荐使用 Optional.ofNullable()";
    public static final String OPTIONAL_FIX_NAME = "Replace with Optional.ofNullable()";

    /** 配置 */
    public static final String TRANSLATION_CONFIGURATION = "请先配置 : File > Setting > Other Settings > JavaCodeHelp > 翻译配置";
    public static final String CODE_STATISTICAL_CONFIGURATION = "请先配置 : File > Setting > Other Settings > JavaCodeHelp > 代码统计 > 参与统计的文件类型";
    public static final String TEMPLATE_CONFIGURATION = "请先配置 : File > Setting > Other Settings > JavaCodeHelp > 模版配置 > 自定义模版配置";
    public static final String TEMPLATE_AUTHOR_CONFIGURATION = "请先配置 : File > Setting > Other Settings > JavaCodeHelp > 模版配置 > 作者";

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

    /** 工具 */
    public static final String GENERATE_QR_CODE_FIRST = "请先生成二维码";
    public static final String UPLOAD_QR_CODE_FIRST = "请先上传二维码";
    public static final String NOT_CRON_EXPRESSIONS = "非cron表达式";
    public static final String AES_SECRET_KEY_LENGTH_ERROR = "秘钥长度必须为16位";
    public static final String AES_IV_LENGTH_ERROR = "偏移量长度必须为16位";
    public static final String FORMAT_ERROR = "格式错误";

    /** 导出 */
    public static final String TABLE_EXPORT_PATH_NOT_NULL = "文件导出路径不能为空";
    public static final String EXPORT_SUCCESS = "导出成功";

}
