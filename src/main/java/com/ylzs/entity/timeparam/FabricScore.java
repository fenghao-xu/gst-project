package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-04 9:47
 * 面料分值
 */
@Data
public class FabricScore extends SuperEntity {
    /**
     * 面料分值方案编码
     */
    private String fabricScorePlanCode;
    /**
     * 面料分值名称
     */
    private String fabricScoreName;
    /**
     * 面料分值编码
     */
    private String fabricScoreCode;
    /**
     * 备注
     */
    private String remark;
    /**
     * 分值范围
     */
    private String scoreRange;
    /**
     * 面料等级编码
     */
    private String fabricGradeCode;

    /**
     * 最大值
     */
    private Integer maxValue;

    /**
     * 最小值
     */
    private Integer minValue;
}
