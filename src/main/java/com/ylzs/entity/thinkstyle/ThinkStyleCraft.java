package com.ylzs.entity.thinkstyle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @description: 智库款工艺工序
 * @author: lyq
 * @date: 2020-03-05 11:53
 */

@Data
public class ThinkStyleCraft extends SuperEntity {
    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 是否新增 0是 1否 （是否新增的工序）
     */
    private Boolean isNew;

    /**
     * 智库款关联代码（智库款think_style_warehouse的random_code）
     */
    private Long styleRandomCode;

    /**
     * 上级部件关联代码（智库款部件think_style_part的random_code）
     */
    private Long partRandomCode;

    /**
     * 工序关联代码（缝制工序词库sewing_craft_warehouse的random_code）
     */
    private Long craftRandomCode;

    /**
     * 工序代码
     */
    private String craftCode;

    /**
     * 工序名称
     */
    private String craftName;

    /**
     * 工序描述
     */
    private String craftDesc;

    /**
     * 工序流
     */
    private Integer craftFlowNum;


    /**
     * 备注
     */
    private String remark;

    /**
     * 设计部件代码
     */
    @TableField(exist = false)
    private transient String designPartCode;
    /**
     * 设计部件-部件位置代码
     */
    @TableField(exist = false)
    private transient String positionCode;

    /**
     * 来源工序,临时使用
     */
    private transient String sourceCraftName;
}

