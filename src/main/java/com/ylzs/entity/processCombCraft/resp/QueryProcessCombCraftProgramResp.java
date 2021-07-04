package com.ylzs.entity.processCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 页面展示工序组合工艺方案数据
 * @Date 2020/3/16
 */
@Data
public class QueryProcessCombCraftProgramResp implements Serializable {
    private static final long serialVersionUID = -1999502801836646347L;

    /**
     * 工序组合工艺编码和名称
     */
    private String processCraftCodesAndNames;

    /**
     * 组合方案编号
     */
    private Integer processNumber;

    /**
     * 工序下标
     */
    private Integer processIndex;
}
