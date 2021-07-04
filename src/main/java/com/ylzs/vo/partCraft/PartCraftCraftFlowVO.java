package com.ylzs.vo.partCraft;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-06-21 15:01
 */
@Data
public class PartCraftCraftFlowVO {
    /**
     * 部件工艺标准时间之和，单位分钟
     */
    private BigDecimal standardTime;
    /**
     * 部件工艺标准单价之和，单位元
     */
    private BigDecimal standardPrice;

    /**
     * 工序流
     */
    private String craftFlowNum;


    /**
     * 工序代码
     */
    private String craftCode;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;

    /**
     * 工序描述
     */
    private String craftRemark;

    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;

    /**
     * 工艺品类code
     */
    private String craftCategoryCode;

    /**
     * 主框架名称
     */
    private transient String mainFrameName;

    /**
     * 主框架编码
     */
    private transient String mainFrameCode;

}
