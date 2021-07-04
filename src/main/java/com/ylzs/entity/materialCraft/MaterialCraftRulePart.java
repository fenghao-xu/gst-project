package com.ylzs.entity.materialCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 材料工艺特殊规则对应部件表
 * @Date 2020/3/5
 */
@Data
public class MaterialCraftRulePart extends SuperEntity {
    private static final long serialVersionUID = 6558781800137798902L;

    /**
     * 材料工艺主数据material_craft的random_code
     */
    private Long materialCraftRandomCode;

    /**
     * 材料规则部件material_craft_rule的random_code,多个用逗号分割
     */
    private String materialCraftRuleRandomCodes;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 方案编号
     */
    private Integer specialPlanNumber;

    /**
     * 设计部件编码
     */
    private String designCode;

    /**
     * 设计编码名称
     */
    private String designName;

    /**
     * 服装品类
     */
    private String clothingCategory;

    /**
     * 部件位置编码
     */
    private String partPositionCode;

    /**
     * 部件位置名称
     */
    private String partPositionName;

    /**
     * 位置下标
     */
    private Integer partIndex;
}
