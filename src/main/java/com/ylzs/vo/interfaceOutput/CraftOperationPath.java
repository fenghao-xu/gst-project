package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CraftOperationPath implements Serializable {
    private static final long serialVersionUID = 6475413839255189334L;
    /**
     * 订单编号
     */
    private String ddbh;
    /**
     * 订单行号
     */
    private String xh;
    /**
     * 款式编号
     */
    private String ksbh;

    /**
     * 站点
     */
    private String site;

    /**
     * 工单号
     */
    private String orderId;

    /**
     * 裁剪工段
     */
    private List<CuttingArrayOp> cuttingArrayOp;

    /**
     * 前置线外工段
     */
    private List<SewingArrayOp> preArrayOp;

    /**
     * 缝制工段
     */
    private List<SewingArrayOp> sewingArrayOp;
}
