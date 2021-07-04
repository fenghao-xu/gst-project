package com.ylzs.entity.staticdata;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-02-27 11:28
 * 不同设备的工序等级
 */
@Data
public class CraftGradeEquipment extends SuperEntity {
    /**
     * 类型
     */
    private String type;

    /**
     * 工序店等级编码
     */
    private String craftGradeCode;

    /**
     * 工序等级名称
     */
    private String craftGradeName;

    /**
     * 工序调整系数(分钟)
     */
    private BigDecimal craftAdjustmentFactor;

    /**
     * 小时工资(元/时)
     */
    private BigDecimal hourlyWage;

    /**
     * 分钟工资（元/分钟)
     */
    private BigDecimal minuteWage;

    /**
     * 工厂编码
     */
    private String factoryCode;

    /**
     * 工段
     */
    private String section;

    /**
     * 备注
     */
    private String remark;
}
