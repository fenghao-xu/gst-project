package com.ylzs.entity.processCombCraft.req;

import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺请求数据
 * @Date 2020/3/14
 */
@Data
public class ProcessCombCraftReq implements Serializable {

    private static final long serialVersionUID = -4783097551572498175L;

    /**
     * 工序组合工艺
     */
    private ProcessCombCraft processCombCraft;

    /**
     * 工序组合工艺规则
     */
    private List<ProcessCombCraftRule> rules;

    /**
     * 工序组合工艺方案
     */
    private List<ProcessCombCraftProgramReq> programReq;

    /**
     * 用户编码
     */
    private String userCode;

}
