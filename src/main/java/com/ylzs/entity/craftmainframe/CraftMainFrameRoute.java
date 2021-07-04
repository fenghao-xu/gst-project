package com.ylzs.entity.craftmainframe;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

@Data
public class CraftMainFrameRoute extends SuperEntity {
    /**
    * 主框架关联代码
    */
    private Long mainFrameRandomCode;

    /**
    * 当前生产部件关联代码
    */
    private Long productionPartRandomCode;

    /**
    * 后续生产部件关联代码
    */
    private Long nextProductionPartRandomCode;

    /**
    * 当前生产部件代码
    */
    private String productionPartCode;

    /**
    * 当前生产部件名称
    */
    private String productionPartName;

    /**
    * 后续生产部件代码
    */
    private String nextProductionPartCode;

    /**
    * 后续生产部件名称
    */
    private String nextProductionPartName;
}