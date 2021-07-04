package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThinkStyleFabricVo implements Serializable {
    private static final long serialVersionUID = 8356484293340509590L;


    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 是否主面料
     */
    private Boolean isMain;

    /**
     * 材料编码
     */
    private String materialCode;

    /**
     * 材料名称
     */
    private String materialName;

    /**
     * 面料分值
     */
    private String fabricScore;

    /**
     * 克重等级
     */
    private String weightGrade;

    /**
     * 纬向弹力等级
     */
    private String latElasticGrade;

    /**
     * 经向弹力等级
     */
    private String merElasticGrade;

    /**
     * 纱线滑移性
     */
    private String yarnSlippage;

    /**
     * 物料中类
     */
    private String materialClass;

    /**
     * 条格类型
     */
    private String barType;

    /**
     * 条格类型名称
     */
    private String barTypeName;

    /**
     * 面料颜色
     */
    private String fabricColor;
}
