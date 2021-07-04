package com.ylzs.entity.staticdata;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-03-27 10:00
 */
@Data
public class PatternSymmetry extends SuperEntity {
    private Long id;
    private String name;
    private String code;
    private BigDecimal sewingRatio;
}
