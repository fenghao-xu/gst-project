package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-03-03 17:25
 * 面料等级
 */
@Data
public class FabricGrade extends SuperEntity {
    /**
     * 面料等级编码
     */
    private String fabricGradeCode;
    /**
     * 面料等级名称
     */
    private String fabircGradeName;
    /**
     * 系数
     */
    private BigDecimal coefficient;
    /**
     * 备注
     */
    private String remark;
}
