package com.ylzs.entity.thinkstyle;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.util.Date;

@Data
public class ThinkStyleCraftHistory extends SuperEntity {
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
     * 移除用户
     */
    private String removeUser;

    /**
     * 移除时间
     */
    private Date removeTime;

    /**
     * 操作方式
     */
    private String operationType;
}