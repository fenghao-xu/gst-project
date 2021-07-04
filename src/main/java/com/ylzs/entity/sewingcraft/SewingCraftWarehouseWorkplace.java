package com.ylzs.entity.sewingcraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-07 17:11
 */
@Data
public class SewingCraftWarehouseWorkplace  {
    /**
     * 工序关联代码--工序词库sewing_craft_warehouse的random_code
     */
    private Long sewingCraftRandomCode;

    /**
     * 工位工序名称
     */
    private String workplaceCraftName;

    /**
     * 工位工序编码
     */
    private String workplaceCraftCode;

    /**
     * 工序流
     */
    private Integer craftFlowNum;

    /**
     * 主框架名称
     */
    private String mainFrameName;

    /**
     * 主框架编码
     */
    private String mainFrameCode;

    private String craftCategoryCode;// 工艺品类编号
    private String craftCategoryName;// 工艺品类名称

    private String partName;//
    private String partCode;//
    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;
    /**
     * 生产部件编码
     */
    private String productionPartCode;
    /**
     * 生产部件名称
     */
    private String productionPartName;

    /**
     * 站位
     */
    private String station;
    /**
     * 站位设备
     */
    private String stationDevice;
}
