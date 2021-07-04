package com.ylzs.entity.thinkstyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 智库款工艺
 * @author: lyq
 * @date: 2020-03-05 11:48
 */

@Data
public class ThinkStyleWarehouse extends SuperEntity {

    /**
     * 服装品类(字典的code)
     */
    private String clothesCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothesCategoryName;


    /**
     * 智库款工艺编码
     */
    private String thinkStyleCode;

    /**
     * 智库款工艺描述
     */
    private String thinkStyleDesc;

    /**
     * 款式编号
     */
    private String styleCode;

    /**
     * 款式名称
     */
    private String styleName;

    /**
     * 款式描述
     */
    private String description;

    /**
     * 款式类型代码(字典的code)
     */
    private String styleTypeCode;

    /**
     * 是否默认用于大货款 0是 1否
     */
    private Boolean isBigStyle;

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
     * 款式图片
     */
    private String pictureUrl;

    /**
     * 工序版本
     */
    private String craftVersion;

    /**
     * 版本说明
     */
    private String versionDesc;

    /**
     * 生效时间
     */
    private Date inspireTime;
    /**
     * 失效时间
     */
    private Date expirationTime;

    /**
     * 发布用户
     */
    private String publishUser;

    /**
     * 发布用户名称
     */
    private String publishUserName;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /**
     * 创建用户名称
     */
    private String createUserName;

    /**
     * 更新用户名称
     */
    private String updateUserName;


    /**
     * 品牌
     */
    private String brand;

    /**
     * 智库款部件列表
     */
    private transient List<ThinkStylePart> partList;


    /**
     * 工艺主框架代码
     */
    private transient String craftMainFrameCode;
    /**
     * 工艺主框架名称
     */
    private transient String craftMainFrameName;

}

