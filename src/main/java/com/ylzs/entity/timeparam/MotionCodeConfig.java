package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-03-04 18:01
 */
@Data
public class MotionCodeConfig extends SuperEntity {
    /**
     * 动作类型代码
     */
    private String typeCode;
    /**
     * 动作类型名称
     */
    private String typeName;

    /**
     * 动作代码
     */
    private String motionCode;
    /**
     * 动作名称
     */
    private String motionName;
    /**
     * 备注
     */
    private String remark;

    /**
     * 人工时间
     */
    private Integer manualTime;
    /**
     * 机器时间
     */
    private Integer machineTime;

    /**
     * 频率
     */
    private BigDecimal frequency;
}
