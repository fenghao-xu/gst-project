package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/11
 */
@Data
public class MaterialCraftSewingCraftResp implements Serializable {
    private static final long serialVersionUID = 550261247682360269L;

    /**
     * 原工序,工序词库sewing_craft_warehouse的craft_code和craft_name
     */
    private String sourceCraftCodeAndName;

    /**
     * 执行工序,工序词库sewing_craft_warehouse的craft_code和craft_name
     */
    private String actionCraftCodeAndName;

    /**
     * 处理方式 -1减少 0替换 1增加
     */
    private Integer processType;

    /**
     * 特殊方案编号
     */
    private Integer specialPlanNumber;

    /**
     * 位置下标
     */
    private Integer ruleIndex;
}
