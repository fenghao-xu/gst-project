package com.ylzs.vo.thinkstyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ThinkStyleWarehouseListVo implements Serializable {
    private static final long serialVersionUID = 4599413422253091485L;
    /**
     * 外部关联代码
     */
    private Long randomCode;
    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 服装品类名称
     */
    private String clothesCategoryName;

    /**
     * 款式编号
     */
    private String styleCode;

    /**
     * 款式名称
     */
    private String styleName;
    /**
     * 标准时间（单位TMU）
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardTime;

    /**
     * 标准单价（单位TMU）
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardPrice;

    /**
     * 款式图片缩略图
     */
    private String pictureUrl;

    /**
     * 创建人
     */
    private String createUserName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
