package com.ylzs.entity.partCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 部件组合工艺规则返回数据
 * @Date 2020/3/13
 */
@Data
public class PartCombCraftRuleResp implements Serializable {
    private static final long serialVersionUID = -7018123686677854828L;
    /**
     * 原工序
     */
    private String sourceCraftCodeAndName;

    /**
     * 执行工序
     */
    private String actionCraftCodeAndName;

    /**
     * 处理方式 -1减少 0替换 1增加
     */
    private Integer processType;

    /**
     * 部件组合工艺part_comb_craft的random_code
     */
    private Long partCombCraftRandomCode;

    /**
     * 下标排序
     */
    private Integer ruleIndex;

    private Integer processingSortNum;

}
