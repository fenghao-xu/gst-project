package com.ylzs.entity.fms;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className Item
 * @Description
 * @Author sky
 * @create 2020-04-24 09:44:39
 */
@Data
public class Item implements Serializable {

    private static final long serialVersionUID = 785962563915794701L;
    /**生产订单号*/
    private String aufnr;
    /**款号*/
    private String matnr;
    /**款号类型*/
    private String extwg;
    /**品类*/
    private String matkl;
    /**品牌*/
    private String brandId;
    /**MTM订单号*/
    private String mtmorder;
    /**生产工厂*/
    private String werks;
    /**生产组别*/
    private String workCenterId;
    /**
     * 适宜性编码
     */
    private String qualf;
    /**生产数量*/
    private String gsmng;
    /**单位*/
    private String gmein;
    /**开始日期*/
    private String gstrs;
    /**开始时间*/
    private String gsuzs;
    /**结束日期*/
    private String gltrs;
   /**结束时间*/
    private String gluzs;
    /**订单紧急程度*/
    private String aprio;
    /**首翻单标识*/
    private String zcutflag;
    /**生产订单状态*/
    private String systemStatus;
    /**款号颜色*/
    private String plnbez;
    /**行项目列表*/
    private List<OrderItem> orderItem;
}
