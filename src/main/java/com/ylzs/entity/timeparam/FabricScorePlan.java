package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-03-04 9:43
 * 面料分值方案
 */
@Data
public class FabricScorePlan extends SuperEntity {
    /**
     * 面料分值方案编码
     */
    private String fabricScorePlanCode;
    /**
     * 面料分值方案名称
     */
    private String fabircScorePlanName;
    /**
     * 备注
     */
    private String remark;
}
