package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-03-03 17:25
 * 面料等级方案
 */
@Data
public class FabricGradeScheme extends SuperEntity {
    /**
     * 面料等级编码
     */
    private String fabricGradeCode;
    /**
     * 面料等级名称
     */
    private String fabircGradeName;

    /**
     * 面料等级方案编码
     */
    private String schemeCode;
    /**
     * 面料等级方案名称
     */
    private String schemeName;

    /**
     * 系数
     */
    private Integer scoreRange;
    /**
     * 备注
     */
    private String remark;

}
