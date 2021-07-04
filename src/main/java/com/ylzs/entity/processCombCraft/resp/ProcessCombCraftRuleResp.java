package com.ylzs.entity.processCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 工序组合工艺规则返回数据
 * @Date 2020/3/13
 */
@Data
public class ProcessCombCraftRuleResp implements Serializable {

    private static final long serialVersionUID = -2307413890014813325L;
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
     * 下标排序
     */
    private Integer ruleIndex;

    private Integer processingSortNum;

}
