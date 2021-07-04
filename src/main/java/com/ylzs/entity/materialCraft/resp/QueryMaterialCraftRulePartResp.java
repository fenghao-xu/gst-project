package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 页面显示材料工艺部件数据
 * @Date 2020/3/20
 */
@Data
public class QueryMaterialCraftRulePartResp implements Serializable {
    private static final long serialVersionUID = -3911465362063661882L;

    /**
     * 方案编号
     */
    private Integer specialPlanNumber;

    /**
     * 设计部件编码和编码名称
     */
    private String designCodeAndName;

    /**
     * 服装品类
     */
    private String clothingCategory;

    /**
     * 部件位置编码
     */
    private String partPositionCodeAndName;

    /**
     * 位置下标
     */
    private Integer partIndex;
}
