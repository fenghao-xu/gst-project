package com.ylzs.vo.thinkstyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ：lyq
 * @description：智库款工艺工序VO
 * @date ：2020-03-07 18:14
 */
@Data
public class ThinkStyleCraftVo implements Serializable {
    private static final long serialVersionUID = -3082325538913913771L;
    /**
     * 关联代码
     */
    private Long randomCode;

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
     * 标准时间
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardTime;


    /**
     * 标准单价
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardPrice;


}
