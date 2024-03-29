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
    public static final String CUSTOMER_TEMPLATE_PATH_CONFIGURATION = "请先配置 : File > Setting > Other Settings > JavaCodeHelp > 自定义模板路径";
    public static final String STATISTICS_IN_PROGRESS = "正在统计中...";

    /** 模版生成代码 */
    public static final String FULL_PATH_CREATE_ERROR = "文件全路径创建失败";
    public static final String CUSTOMER_TEMPLATE_PATH_NOT_EXISTS = "自定义模板路径不存在";
    public static final String CUSTOMER_TEMPLATE_PATH_NOT_FOLDER = "自定义模板路径必须是文件夹";
    public static final String CUSTOMER_TEMPLATE_PATH_NO_FILE = "不存在后缀为.ftl的模版文件";
    public static final String CREATE_FILE_ERROR = "文件创建失败";
    public static final String SQL_NOT_NULL = "Sql不能为空";
    public static final String FULL_PATH_NOT_NULL = "文件全路径不能为空";
    public static final String PACKAGE_PATH_NOT_NULL = "包路径不能为空";

    /** 工具 */
    public static final String GENERATE_QR_CODE_FIRST = "请先生成二维码";
    public static final String UPLOAD_QR_CODE_FIRST = "请先上传二维码";


}
