package com.ylzs.entity.processCombCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 工序组合工艺方案
 * @Date 2020/3/14
 */
@Data
public class ProcessCombCraftProgram extends SuperEntity {

    private static final long serialVersionUID = -5503368121995141684L;
    /**
     * 工序组合工艺process_comb_craft的random_code
     */
    private Long processCombCraftRandomCode;

    /**
     * 工序编码,工序词库sewing_craft_warehouse的craft_code
     */
    private String processCraftCode;

    /**
     * 工序名称,工序词库sewing_craft_warehouse的craft_name
     */
    private String processCraftName;

    /**
     * 组合方案编号
     */
    private Integer processNumber;

    /**
     * 工序下标
     */
    private Integer processIndex;

    /**
     * 备注
     */
    private String remark;

}
