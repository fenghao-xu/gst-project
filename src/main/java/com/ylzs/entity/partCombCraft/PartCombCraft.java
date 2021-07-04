package com.ylzs.entity.partCombCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 部件组合工艺主数据表
 * @Date 2020/3/12
 */
@Data
public class PartCombCraft extends SuperEntity {
    private static final long serialVersionUID = -1462292673058797558L;

    /**
     * 部件组合工艺编码
     */
    private String partCombCraftCode;

    /**
     * 部件组合工艺名称
     */
    private String partCombCraftName;

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
    private String partCombCraftDesc;

    /**
     * 部件组合工艺方案,多个用逗号分割
     */
    private String partCombCraftNumber;

    /**
     * 备注
     */
    private String remark;

}
