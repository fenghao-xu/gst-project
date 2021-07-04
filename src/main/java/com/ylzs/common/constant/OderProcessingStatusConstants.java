package com.ylzs.common.constant;

/**
 * @author xuwei
 * @create 2020-07-18 16:29
 */
public class OderProcessingStatusConstants {
    /**
     * 大货
     */
    public interface BigStyleStatus {
        /**
         * 接收大货款式
         */
        public static final Integer BIG_STYLE_1120 = 1120;

        /**
         * 接收裁剪参数
         */
        public static final Integer BIG_STYLE_1140 = 1140;
        /**
         * 发送工段工时至FMS
         */
        public static final Integer BIG_STYLE_1250 = 1250;
        /**
         * 接收工单信息
         */
        public static final Integer BIG_STYLE_1260 = 1260;
        /**
         * 工序路径生成并传送ME
         */
        public static final Integer BIG_STYLE_1270 = 1270;
    }

    /**
     * 大货
     */
    public interface BigStyleStatusName {
        /**
         * 接收大货款式
         */
        public static final String BIG_STYLE_NAME_1120 = "接收大货款式";

        /**
         * 接收裁剪参数
         */
        public static final String BIG_STYLE_NAME_1140 = "接收裁剪参数";
        /**
         * 发送工段工时至FMS
         */
        public static final String BIG_STYLE_NAME_1250 = "发送工段工时至FMS";
        /**
         * 接收工单信息
         */
        public static final String BIG_STYLE_NAME_1260 = "接收工单信息";
        /**
         * 工序路径生成并传送ME
         */
        public static final String BIG_STYLE_NAME_1270 = "工序路径生成并传送ME";
    }

    /**
     * 定制
     */
    public interface CusOrderStatus {
        /**
         * 接收定制订单
         */
        public static final Integer CUS_ORDER_1100 = 1100;
        /**
         * 接收靠码定制订单
         */
        public static final Integer CUS_ORDER_1110 = 1110;
        /**
         * 缺少裁剪参数或者缝边位，请处理！
         */
        public static final Integer CUS_ORDER_1130 = 1130;
        /**
         * 接收裁剪参数
         */
        public static final Integer CUS_ORDER_1140 = 1140;
        /**
         * 智库款未发布，请处理！
         */
        public static final Integer CUS_ORDER_1150 = 1150;
        /**
         * 靠码定制款的大货款工序路径未发布
         */
        public static final Integer CUS_ORDER_1160 = 1160;
        /**
         * 有部件没有发布XXXX（X表示部件编码，多个部件编码用，隔开）/有部件位置没有发布SSSS（S表示部件位置编码，多个部件位置用，隔开）
         */
        public static final Integer CUS_ORDER_1170 = 1170;
        /**
         * 隐线部件未发布，请处理！
         */
        public static final Integer CUS_ORDER_1180 = 1180;
        /**
         * 部件组合未发布XXXX（X表示部件组合编码，多个部件组合编码用，隔开）
         */
        public static final Integer CUS_ORDER_1190 = 1190;
        /**
         * 工序组合未发布XXXX（X表示工序组合编码，多个工序组合编码用，隔开）
         */
        public static final Integer CUS_ORDER_1200 = 1200;
        /**
         * 材料工艺未发布XXXX（X表示材料编码，多个材料编码用，隔开）
         */
        public static final Integer CUS_ORDER_1210 = 1210;
        /**
         * 生成订单工序路径
         */
        public static final Integer CUS_ORDER_1220 = 1220;
        /**
         * 订单工序路径无完成标识，请处理！
         */
        public static final Integer CUS_ORDER_1230 = 1230;
        /**
         * 订单工序路径存在相同工序流，请处理！
         */
        public static final Integer CUS_ORDER_1240 = 1240;
        /**
         * 发送工段工时至FMS
         */
        public static final Integer CUS_ORDER_1250 = 1250;
        /**
         * 接收工单信息
         */
        public static final Integer CUS_ORDER_1260 = 1260;
        /**
         * 工序路径生成并传送ME
         */
        public static final Integer CUS_ORDER_1270 = 1270;
        /**
         * 发送订单状态至MTM 
         */
        public static final Integer CUS_ORDER_1280 = 1280;

    }

    /**
     * 定制
     */
    public interface CusOrderStatusName {
        public static final String CUS_ORDER_NAME_1100 = "接收定制订单";
        public static final String CUS_ORDER_NAME_1110 = "接收靠码定制订单";
        public static final String CUS_ORDER_NAME_1130 = "缺少裁剪参数或者缝边位，请处理";
        public static final String CUS_ORDER_NAME_1140 = "接收裁剪参数";
        public static final String CUS_ORDER_NAME_1150 = "智库款未发布，请处理";
        public static final String CUS_ORDER_NAME_1160 = "靠码定制款的大货款工序路径未发布";
        public static final String CUS_ORDER_NAME_1170 = "有部件没有发布XXXX（X表示部件编码，多个部件编码用，隔开）/有部件位置没有发布SSSS（S表示部件位置编码，多个部件位置用，隔开）";
        public static final String CUS_ORDER_NAME_1180 = "隐线部件未发布，请处理！";
        public static final String CUS_ORDER_NAME_1190 = "部件组合未发布XXXX（X表示部件组合编码，多个部件组合编码用，隔开）";
        public static final String CUS_ORDER_NAME_1200 = "工序组合未发布XXXX（X表示工序组合编码，多个工序组合编码用，隔开）";
        public static final String CUS_ORDER_NAME_1210 = "材料工艺未发布XXXX（X表示材料编码，多个材料编码用，隔开）";
        public static final String CUS_ORDER_NAME_1220 = "生成订单工序路径";
        public static final String CUS_ORDER_NAME_1230 = "订单工序路径无完成标识，请处理！";
        public static final String CUS_ORDER_NAME_1240 = "订单工序路径存在相同工序流，请处理！";
        public static final String CUS_ORDER_NAME_1250 = "发送工段工时至FMS";
        public static final String CUS_ORDER_NAME_1260 = "接收工单信息";
        public static final String CUS_ORDER_NAME_1270 = "工序路径生成并传送ME";
        public static final String CUS_ORDER_NAME_1280 = "发送订单状态至MTM";
    }
}
