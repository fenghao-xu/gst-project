package com.ylzs.entity.workplacecraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

@Data
public class WorkplaceCraft extends SuperEntity {
    /**
     * 工位工序代码
     */
    private String workplaceCraftCode;

    /**
     * 工位工序名称
     */
    private String workplaceCraftName;

    /**
     * 工序流（数字，隔100）
     */
    private Integer craftFlowNum;
    /**
     * 是否默认工位工序,1----是，0--否
     */
    private Integer isDefault;
    /**
     * 生产部件production_part的random_code
     */
    private Long productionPartRandomCode;

    /**
     * 生产部件代码
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
     * 工艺品类名称
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
     * 审核用户名称
     */
    private String auditUserName;
    /**
     * 创建用户名称
     */
    private String createUserName;
    /**
     * 更新用户名称
     */
    private String updateUserName;
    /**
     * 站位
     */
    private String station;
    /**
     * 站位设备
     */
    private String stationDevice;

}