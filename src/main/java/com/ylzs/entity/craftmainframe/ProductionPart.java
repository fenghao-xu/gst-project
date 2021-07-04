package com.ylzs.entity.craftmainframe;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

@Data
public class ProductionPart extends SuperEntity {
    /**
     * 生产部件编码（生产部件唯一标识）
     */
    private String productionPartCode;

    /**
     * 生产部件名称
     */
    private String productionPartName;

    /**
     * 工艺主框架关联代码
     */
    private Long mainFrameRandomCode;

    /**
     * 工艺主框架代码
     */
    private String mainFrameCode;

    /**
     * 工艺主框架名称
     */
    private String mainFrameName;

    /**
     * 工艺品类关联代码
     */
    private Long craftCategoryRandomCode;

    /**
     * 工艺品类编码
     */
    private String craftCategoryCode;

    /**
     * 工艺品类名称
     */
    private String craftCategoryName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 定制生产区代码
     */
    private String customProductionAreaCode;
    /**
     * 定制生产区名称
     */
    private String customProductionAreaName;
}