package com.ylzs.entity.processCombCraft.resp;

import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 工序组合工艺返回数据
 * @Date 2020/3/16
 */
@Data
public class ProcessCombCraftData implements Serializable {
    private static final long serialVersionUID = 2253681706095180829L;

    /**
     * 工序组合工艺数据
     */
    private ProcessCombCraft combCraft;

    /**
     * 页面展示工序组合工艺方案数据
     */
    private Map<Integer, List<ProcessCombCraftProgram>> programResps;

    /**
     * 规则
     */
    private List<ProcessCombCraftRule> ruleList;
}
