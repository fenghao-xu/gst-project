package com.ylzs.entity.processCombCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 工序组合工艺主数据
 * @Date 2020/3/14
 */
@Data
public class ProcessCombCraft extends SuperEntity {

    private static final long serialVersionUID = 4797436795095375366L;
    /**
     * 工序组合工艺编码
     */
    private String processCombCraftCode;

    /**
     * 工序组合工艺名称
     */
    private String processCombCraftName;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

    /**
     * 描述
     */
    private String processCombCraftDesc;

    /**
     * 工序组合工艺方案编号，多个用逗号分割
     */
    private String processNumbers;

    /**
     * 备注
     */
    private String remark;

}
