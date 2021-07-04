package com.ylzs.entity.sewingcraft.relation;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author weikang
 * @Description 工序关联的工序数据
 * @Date 2021/1/19 16:45
 */
@Data
public class SewingCraftWarehouseRelationResp implements Serializable {

    private static final long serialVersionUID = 1221638779332599926L;

    /**
     * 关联工序表sewing_craft_warehouse_relation的random_code
     */
    private Long sewingCraftRelationRandomCode;

    /**
     * 车缝工序词库的random_code
     */
    private Long sewingCraftRandomCode;

    /**
     * 工序编号
     */
    private String craftCode;

    /**
     * 工序描述
     */
    private String description;

    /**
     * 工序等级
     */
    private String craftGradeCode;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 标准时间
     */
    private BigDecimal standardTime;

    /**
     * 标准单价
     */
    private BigDecimal standardPrice;

    /**
     * 是否有关联工序 true 有 false没有
     */
    private boolean havingCraftRelation;

    /**
     * 工序流
     */
    private Integer craftFlowNum;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 工艺品类
     */
    private String craftCategoryCode;
}
