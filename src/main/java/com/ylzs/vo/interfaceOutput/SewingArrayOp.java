package com.ylzs.vo.interfaceOutput;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 缝制工段
 */
@Data
public class SewingArrayOp implements Serializable {
    private static final long serialVersionUID = 5932583371147793529L;
    /**
     * 生产部件代码
     */
    private String scbjdm;
    /**
     * 生产部件名称
     */
    private String scbjmc;
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
     * 工序描述
     */
    private String gxms;

    /**
     * 结构部件代码
     */
    private String bjdm;

    /**
     * 结构部件名称
     */
    private String bjmc;

    /**
     * 工序时间
     */
    @JSONField(name="SMV")
    private String SMV;

    /**
     * 单价
     */
    private String dj;

    /**
     * 组合号
     */
    private String zhxh;

    /**
     * 品质要求
     */
    private String pzsm;

    /**
     * 工艺说明
     */
    private String gzsm;

    /**
     * 部件小类
     */
    private String mtmbjdm;

    /**
     * 上岗证代码
     */
    private String sgzdm;

    /**
     * 上岗证名称
     */
    private String sgzmc;

    /**
     * 图案工艺
     */
    private String pattDesc;

    /**
     * 设计部件URL地址
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

    private String mtmpart;
    /**
     * 工艺图片url
     */
    private String fjPath;
    /**
     * 工艺视频url
     */
    private String videoURL;

    private String bmdm;
    private String bmmc;
    private String gddm;
    private String gdmc;
    private String zglb;
    private String zgmc;
    private String bmxh;
    private String scbjxh;
    private String zgxh;
    private String scbj;






}


