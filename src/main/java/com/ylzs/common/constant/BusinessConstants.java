package com.ylzs.common.constant;

/**
 * @author xuwei
 * @create 2020-02-27 11:47
 */
public class BusinessConstants {
    /**
     * 工序等级的常量
     */
    public interface CraftGradeEquipmentType {
        /**
         * 车缝工序等级
         */
        public static final String CRAFT_GRADE_SEWING = "sewing";

        /**
         * 裁床工序等级
         */
        public static final String CRAFT_GRADE_CUTTING_BED = "cuttingBed";
    }

    /**
     * 适应性编码
     */
    public interface AdaptCode {
        /**
         * 智能产线
         */
        public static final String WISDOM_LINE = "10";
        /**
         * 智能产线
         */
        public static final String WISDOM_LINE_TEN_NAME= "智能产线";

        /**
         * 智能产线
         */
        public static final String NONE_WISDOM_LINE_NAME= "非智能产线";

    }
    public interface Status {
        /**
         * 草稿状态
         */
        public static final Integer DRAFT_STATUS = 1000;

        /**
         * 已审核状态
         */
        public static final Integer AUDITED_STATUS = 1010;

        /**
         * 已发布状态
         */
        public static final Integer PUBLISHED_STATUS = 1020;

        /**
         * 失效状态
         */
        public static final Integer INVALID_STATUS = 1030;

        /**
         * 未生效
         */
        public static final Integer NOT_ACTIVE_STATUS = 1040;

        /**
         * 异常
         */
        public static final Integer ORDER_EXCEPTION = 1050;

        /**
         * 删除标识
         */
        public static final Integer IN_VALID = 1090;
    }

    /**
     * 业务类型
     */
    public interface BusinessType {

        /**
         * 定制
         */
        public static final String CUSTOMIZE = "定制";

        /**
         * 大货
         */
        public static final String BIG_STYLE = "大货";

        /**
         * 工单工艺
         */
        public static final Integer WORKNO_CRAFT = 20;

        /**
         * 大货款式工艺
         */
        public static final Integer STYLE_CRAFT = 10;


    }

    /**
     * 常数
     */
    public interface CommonConstant {
        int ZERO = 0;
        int ONE = 1;
        int TWO = 2;
        int THREE = 3;
        int FOUR = 4;
        int FIVE = 5;
        int SIX = 6;
        int SEVEN = 7;
        int EIGHT = 8;
        int NINE = 9;
        int TEN = 10;
    }

    public interface SewingParam {
        /**
         * 停车时间
         */
        public static final String STOP_TIME = "StopTime";
        /**
         * 线迹系数时间
         */
        public static final String XIAN_JI_RATION = "XianJiRation";
        /**
         * 工种
         */
        public static final String WORK_TYPE = "WorkType";
    }

    public interface Redis {
        /**
         * 车缝工序词库分布式锁的key
         */
        public static final String SEWING_CRAFT_LOCK_KEY = "sewingcraft:lockkey";


        /**
         * 保存车缝工序词库流水号的key
         */
        public static final String STORE_SEW_CRAFT_SERIAL_NUMBER = "sewingcraft:serials";

        /**
         * 款式分析分布式锁的key
         */
        public static final String BIG_STYLE_ANALYSE_LOCK_KEY = "bigStyleAnalyse:lockkey";


        /**
         * 款式分析流水号的key
         */
        public static final String STORE_BIG_STYLE_ANALYSE_SERIAL_NUMBER = "bigStyleAnalyse:serials";

        /**
         * 部件工艺主数据分布式锁key
         */
        public static final String PART_CRAFT_LOCK_KEY = "partcraft:key";

        /**
         * 保存部件主数据流水号key
         */
        public static final String STORE_PART_CRAFT_SEARIAL = "partcraft:serials::";

        /**
         * 材料工艺编码分布式锁key
         */
        public static final String MATERIAL_CRAFT_LOCK_KEY = "material:key";

        /**
         * 材料工艺编码的key
         */
        public static final String MATERIAL_CRAFT_SERIAL_NUMBER = "material:serials";

        /**
         * 部件组合工艺编码分布式锁key
         */
        public static final String PART_COMB_CRAFT_LOCK_KEY = "part_comb:key";

        /**
         * 部件组合工艺编码的key
         */
        public static final String PART_COMB_CRAFT_SERIAL_NUMBER = "part_comb:serials";

        /**
         * 工序组合工艺编码分布式锁key
         */
        public static final String PROCESS_COMB_CRAFT_LOCK_KEY = "process_comb:key";

        /**
         * 工序组合工艺编码的key
         */
        public static final String PROCESS_COMB_CRAFT_SERIAL_NUMBER = "process_comb:serials";

    }

    /**
     * 字典值
     */
    public interface DictionaryType {
        /**
         * 服装大类
         */
        public static final String CLOTHES_CATEGORY = "ClothesCategory";
        /**
         * 子品牌
         */
        public static final String SUB_BRAND = "SubBrand";
        /**
         * 包装方式
         */
        public static final String PACKING_METHOD = "PackingMethod";
    }

    /**
     * 操作哪种业务里面的工序的表，工序管理，款式工艺 都要新增工序数据
     */
    public interface TableName {
        /**
         * 款式分析的
         */
        public static final String BIG_STYLE_ANALYSE = "big_style_analyse";
        /**
         * 工序管理
         */
        public static final String SEWING_CRAFT = "sewing_craft";

        /**
         * 工单工艺
         */
        public static final String BIG_ORDER = "big_order";
    }

    public interface Prefix {

        /**
         * 部件组合工艺前缀
         */
        public static final String PART_COMB_PREFIX = "UC";

        /**
         * 工序组合工艺前缀
         */
        public static final String PROCESS_COMB_PREFIX = "PC";
    }

    public interface Send2Pi {
        /**
         * 传输包中数据条数的状态--新增
         */
        public static final String actionType_create = "create";

        /**
         * 传输包中数据条数的状态--修改
         */
        public static final String actionType_update = "update";

        /**
         * 传输包中数据条数的状态--删除
         */
        public static final String actionType_delete = "delete";
    }

    public interface ReceivePiLog {

        /**
         * 接收类型--面料主数据
         */
        public static final String receiveType_fabricMasterData = "材料（面料）主数据";

        /**
         * 接收类型--品类主数据
         */
        public static final String receiveType_categoryMasterData = "字典数据主数据";

        /**
         * 接收类型--部件主数据
         */
        public static final String receiveType_partMasterData = "部件主数据";

        /**
         * 接收类型--智库款主数据
         */
        public static final String receiveType_productModelData = "智库款主数据";

        /**
         * 接收类型--智库大货款
         */
        public static final String receiveType_mbsData = "智库大货款";
        /**
         * 接收类型--定制款裁剪参数
         */
        public static final String receiveType_customcropparam = "定制款裁剪参数";

        /**
         * 接收类型--裁剪粘补方式
         */
        public static final String receiveType_cropstickingway = "裁剪粘补方式";

        /**
         * 接收类型--定制订单
         */
        public static final String receiveType_mtmorder = "定制订单";

        /**
         * 接收类型--预排产结果
         */
        public static final String receiveType_preschedureresult = "预排产结果";

        /**
         * 接收类型--试产结果
         */
        public static final String receiveType_pilotproductionresult = "试产反馈信息";

    }


    /**
     * PI 接收地址信息
     */
    public interface PiReceiveUrlConfig {
        /**
         * 授权APP
         */
        public static final String URL_AUTH_APP = "urlAuth_app";
        /**
         * 授权密钥
         */
        public static final String URL_AUTH_SECRET = "urlAuth_secret";

        /**
         * 智库款发布信息url
         */
        public static final String THINK_STYLE_PUBLISH_URL = "thinkStylePublish_url";
        /**
         * 工序url
         */
        public static final String SEWING_CRAFT_URL = "sewcraft_url";

        /**
         * 定制款操作路线URL
         */
        public static final String CUSTOM_STYLE_OPERATION_PATH_URL = "customStyleOperationPath_url";

        /**
         * 大货款预估工段工时
         */
        public static final String BIG_STYLE_PRE_SECTION_SMV_URL = "bigStylePreSectionSMV_url";

        /**
         * 机器地址
         */
        public static final String MACHINE_URL = "machine_url";

        /**
         * GST资源存放路径
         */
        public static final String GST_RESOURCE_URL = "gstResource_url";

        /**
         * 新工序
         */
        public static final String NEW_CRAFT_URL = "newCraft_url";
        /**
         * 大货款智化标识
         */
        public static final String BIG_ORDER_THINK_FLAG_URL = "bigOrderThinkFlag_url";
    }

    public interface ExportTemplate {
        /**
         * 10有价格
         */
        public static final Integer HAVE_PRICE = 10;
        /**
         * 无价格模板
         */
        public static final Integer NOT_HAVE_PRICE = 20;

        /**
         * 10有价格
         */
        public static final Integer EPS_HAVE_PRICE = 30;
        /**
         * 无价格模板
         */
        public static final Integer EPS_NOT_HAVE_PRICE = 40;

        /**
         * 财务报表
         */
        public static final Integer FINANCE = 50;

    }

    public interface OrderType {
        /**
         * 工单工艺
         */
        public static final String BIG_ORDER = "10";
        /**
         * 订单工艺
         */
        public static final String CUSTOMER_OIRDER = "20";

    }

    public interface FromSysType {
        /**
         * 来源GST
         */
        public static final String FROM_GST = "1";

        /**
         * 来源CAPP
         */
        public static final String FROM_CAPP = "2";
    }

}
