package com.ylzs.entity.partCombCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 部件组合工艺规则表
 * @Date 2020/3/12
 */
@Data
public class PartCombCraftRule extends SuperEntity {

    private static final long serialVersionUID = -1608442489611922920L;

    /**
     * 部件组合工艺part_comb_craft的random_code
     */
    private Long partCombCraftRandomCode;

    /**
     * 原工序编码,工序词库sewing_craft_warehouse的craft_code,多个用#分割
     */
    private String sourceCraftCode;

    /**
     * 原工序名称,工序词库sewing_craft_warehouse的craft_name,多个用#分割
     */
    private String sourceCraftName;

    /**
     * 执行工序代码,工序词库sewing_craft_warehouse的craft_code,多个用#分割
     */
    private String actionCraftCode;

    /**
     *执行工序名称,工序词库sewing_craft_warehouse的craft_name,多个用#分割
     */
    private String actionCraftName;

    /**
     * 处理方式 -1减少 0替换 1增加
     */
    private Integer processType;

    /**
     * 描述信息
     */
    private String remark;

    /**
     * 下标排序
     */
    private Integer ruleIndex;

    private Integer processingSortNum;

}
