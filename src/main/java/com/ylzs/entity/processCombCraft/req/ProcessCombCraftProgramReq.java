package com.ylzs.entity.processCombCraft.req;

import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺方案请求数据
 * @Date 2020/3/12
 */
@Data
public class ProcessCombCraftProgramReq implements Serializable {

    private static final long serialVersionUID = -6546385570556119658L;

    /**
     * 方案
     */
    private List<ProcessCombCraftProgram> programs;

    /**
     * 工序编码,工序词库sewing_craft_warehouse的craft_code，多个用逗号分割
     */
    private String processCraftCodes;

    /**
     * 方案编码
     */
    private Integer processNumber;
}
