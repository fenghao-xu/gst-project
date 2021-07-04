package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;

/**
 * 裁剪工段
 */
@Data
public class CuttingArrayOp implements Serializable {
    private static final long serialVersionUID = 4951895751442898765L;
    /**
     * 序号
     */
    private String xh;
    /**
     * 工序代码
     */
    private String gxdm;
    /**
     * 后续工序代码
     */
    private String hxgxdm;

    /**
     * 工序名称
     */
    private String gxmc;

    /**
     * 设计部件代码
     */
    private String bjdm;

    /**
     * 设计部件名称
     */
    private String bjmc;
    /**
     * 工序时间
     */
    private String smv;
    /**
     * 单价
     */
    private String dj;
    /**
     * 品质要求
     */
    private String pzsm;
    /**
     * 工艺说明
     */
    private String gzsm;
    /**
     * 上岗证代码
     */
    private String sgzdm;
    /**
     * 图案工艺
     */
    private String pattDesc;
    /**
     * URL地址
     */
    private String imageURL;
    /**
     * 绣花部件
     */
    private String bjdm1;

    /**
     * 绣花部件名称
     */
    private String bjmc1;

    /**
     * 工艺图地址
     */
    private String fjPath;

    /**
     * 工艺视频地址
     */
    private String videoURL;

    /**
     * 裁剪并行组后续工序
     */
    private String groupnext;

    /**
     * 裁剪并行工序组
     */
    private String groupchild;
    /**
     * 裁剪并行工序组（数组）
     */
    //private String operationGroup;
    //private String nextGOperationSet;
    /**
     * 裁剪并行工序数组
     */
    //private String subOperationList;
    /**
     * 并行工序代码
     */
    //private String subOperation;
}
