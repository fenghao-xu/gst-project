package com.ylzs.entity.thinkstyle;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @description: 智库款工艺面料
 * @author: lyq
 * @date: 2020-03-05 18:08
 */

@Data
public class ThinkStyleFabric extends SuperEntity {
    /**
     * 行号
     */
    private Integer lineNo;

    /**
     * 智库款关联代码
     */
    private Long styleRandomCode;

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
     * 面料颜色
     */
    private String fabricColor;

}

