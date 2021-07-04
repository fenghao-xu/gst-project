package com.ylzs.entity.partCombCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 组合工艺部件和位置详情数据
 * @Date 2020/3/12
 */
@Data
public class PartCombCraftProgramDetail extends SuperEntity {

    private static final long serialVersionUID = 4557170428619486991L;

    /**
     * 部件组合工艺part_comb_craft的random_code
     */
    private Long partCombCraftRandomCode;

    /**
     * 设计部件编码
     */
    private String designCode;

    /**
     * 设计部件名称
     */
    private String designName;

    /**
     * 部件位置编码
     */
    private String partPositionCode;

    /**
     * 部件位置名称
     */
    private String partPositionName;

    /**
     * 方案编码
     */
    private Integer partNumber;

    /**
     * 位置下标
     */
    private Integer partDetailIndex;

    /**
     * 备注
     */
    private String remark;

}
