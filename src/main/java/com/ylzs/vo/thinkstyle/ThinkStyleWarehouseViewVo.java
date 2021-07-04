package com.ylzs.vo.thinkstyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 智库款信息详情页面
 */
@Data
public class ThinkStyleWarehouseViewVo implements Serializable {
    private static final long serialVersionUID = -4428410918786796664L;
    /**
     * 外部关联代码
     */
    private Long randomCode;
    /**
     * 服装品类代码
     */
    private String clothesCategoryCode;
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
     * 智库款工艺编码
     */
    private String thinkStyleCode;

    /**
     * 智库款工艺描述
     */
    private String thinkStyleDesc;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 发布用户
     */
    private String publishUserName;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
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
    /**
     * 维护人
     */

    private String updateUserName;
    /**
     * 维护时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
