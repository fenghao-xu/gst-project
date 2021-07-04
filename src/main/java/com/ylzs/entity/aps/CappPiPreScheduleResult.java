package com.ylzs.entity.aps;

import lombok.Data;

import java.util.Date;

@Data
public class CappPiPreScheduleResult {
    private Long id;

    /**
     * 只发大货的，排除掉“定制款”不发
     */
    private String purchaseRequisition;

    /**
     * 款号
     */
    private String product;

    /**
     * 排产工厂
     */
    private String planCode;

    /**
     * 数量
     */
    private Double quantity;

    /**
     * 工作中心
     */
    private String workcenterCode;

    /**
     * 预排产开始日期
     */
    private Date scheduleTime;

    /**
     * 预排产交货日期
     */
    private Date deliveryTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 类型 0010 裁剪 0040 车缝
     */
    private String type;

    /**
     * 备注
     */
    private String remark;
}