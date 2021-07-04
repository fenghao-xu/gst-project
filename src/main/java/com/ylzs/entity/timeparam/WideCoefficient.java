package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-02-27 14:48
 * 宽放系数
 */
@Data
public class WideCoefficient extends SuperEntity {

    /**
     * 宽放编码
     */
    private String wideCode;

    /**
     * 宽放名称
     */
    private String wideName;

    /**
     * 系数
     */
    private BigDecimal coefficient;

    /**
     * 工艺品类编码
     */
    private String craftCategoryCode;

    /**
     * 备注
     */
    private String remark;

}
