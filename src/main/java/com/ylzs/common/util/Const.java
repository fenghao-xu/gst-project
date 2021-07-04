package com.ylzs.common.util;

/**
 * 说明：
 *
 * @author Administrator
 * 2019-10-09 10:55
 */
public class Const {

    /**
     * 当前用户在 request 中属性的 key
     */
    public final static String REQUEST_KEY_USER = "__CURRENT_USER";

    /**
     * request 中分页请求对象的key
     */
    public final static String REQ_KEY_PAGE = "___page_request___";

    /**
     * Swagger API 标识的参数类型
     */
    public final static String PARAM_TYPE_QUERY = "query";

    /**
     * 分页查询参数：页码
     */
    public final static String PARAM_NAME_PAGE = "_page";
    public final static String PARAM_DESC_PAGE = "页面，从 0 开始";

    /**
     * 分页查询参数：每页记录数
     */
    public final static String PARAM_NAME_SIZE = "_size";
    public final static String PARAM_DESC_SIZE = "每页最多显示几条";

    /**
     * 分页查询参数：排序
     */
    public final static String PARAM_NAME_SORT = "_sort";
    public final static String PARAM_DESC_SORT = "排序字段，如 +createdDate,-amount，+表示正序，-表示逆序";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss"; // "yyyy-MM-dd HH:mm:ss.SZ"

    /**
     * 缺省页
     */
    public static final String DEFAULT_PAGE = "1";

    /**
     * 缺省行数
     */
    public static final String DEFAULT_ROWS = "30";


    /**
     * 图片文件类型
     */
    public static final int FILE_TYPE_IMG = 1;

    /**
     * 视频文件类型
     */
    public static final int FILE_TYPE_VIDEO = 2;


    /**
     * 机器图片
     */
    public static final int RES_TYPE_MACHINE_IMG = 1;
    /**
     * 效果图
     */
    public static final int RES_TYPE_EFFECT_IMG = 20;
    /**
     * 线稿图
     */
    public static final int RES_TYPE_HAND_IMG = 10;
    /**
     * 演示视频
     */
    public static final int RES_TYPE_STD_VIDEO = 30;


    /**
     * 效果图路径
     */
    public static final String PATH_EFFECT_PIC = "effectPicPath";
    /**
     * 线稿图路径
     */
    public static final String PATH_HAND_PIC = "handPicPath";
    /**
     * 标准视频路径
     */
    public static final String PATH_STD_VIDEO = "videoPath";

    /**
     * 默认输出访问路径前缀
     */
    public static final String HTTP_OUT_PREF = "httpBase";

    /**
     * 上传文件路径
     */
    public static final String UPLOAD_PATH = "uploadPathBase";

    /**
     * 共享路径
     */
    public static final String SHARE_BASE_PATH = "shareBasePath"; //"10.7.200.135/share/";
    /**
     * 共享帐户
     */
    public static final String SHARE_ACC = "shareAcc"; //"naersi\\zoujiangxiong";
    /**
     * 共享密码
     */
    public static final String SHARE_PWD = "sharePwd"; //"yingjiaznb";

    /**
     * 新建状态
     */
    public static final String STD_STATUS_NEW = "0";
    /**
     * 准备好状态
     */
    public static final String STD_STATUS_READY = "1";
    /**
     * 已审核状态
     */
    public static final String STD_STATUS_AUDIT = "2";

    /**
     * 退回
     */
    public static final String STD_STATUS_BACK = "4";

    /**
     * 历史（从GST导过来的）
     */
    public static final String STD_STATUS_OLD = "8";


    /**
     * 提交消息
     */
    public static final byte NOTIFY_MSG_COMMIT = 1;
    /**
     * 撤回消息
     */
    public static final byte NOTIFY_MSG_WITHDRAW = 2;


    /**
     * 通用状态 保存
     */
    public static final int COMMON_STATUS_SAVE = 0;
    /**
     * 通用状态 提交
     */
    public static final int COMMON_STATUS_COMMIT = 1;
    /**
     * 通用状态 审核
     */
    public static final int COMMON_STATUS_AUDIT = 2;
    /**
     * 通用状态 发布
     */
    public static final int COMMON_STATUS_PUBLISH = 3;




    /**
     * 缩略图最大宽度
     */
    public static final String MAX_PIC_WIDTH = "400";
    /**
     * 缩略图最大高度
     */
    public static final String MAX_PIC_HEIGHT = "400";


}
