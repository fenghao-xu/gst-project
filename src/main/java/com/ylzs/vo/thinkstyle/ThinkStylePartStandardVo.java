package com.ylzs.vo.thinkstyle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ThinkStylePartStandardVo implements Serializable {
    private static final long serialVersionUID = -3144806357275299960L;
    /**
     * 随机码
     */
    private Long randomCode;

    /**
     * 标准时间（单位TMU）
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardTime;

    /**
     * 标准单价（单位TMU）
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standarfPrice;
    /**
     * 是否特殊
     */
    private Boolean isSpecial;
    /**
     * 部件位置
     */
    private String partPosition;
    /**
     * 设计部件代码
     */
    String designPartCode;

}
