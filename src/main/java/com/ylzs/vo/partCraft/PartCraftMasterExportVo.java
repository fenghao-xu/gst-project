package com.ylzs.vo.partCraft;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartCraftMasterExportVo implements Serializable {
    private static final long serialVersionUID = -4225107715019864846L;
    /**
     * 部件工艺编码
     */
    //@NonNull
    private String partCraftMainCode;
    /**
     * 部件工艺名称
     */
    private String partCraftMainName;
    /**
     * 工艺品类code
     */
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     * 结构部件，关联工艺部件表craft_part ,craft_part_code字段
     */
    private String craftPartCode;
    /**
     * 结构部件，关联工艺部件名称
     */
    private String craftPartName;
    /**
     * 部件类型，数据字典中获取
     */

    private String partType;
    /**
     * 部件工艺标准时间之和，单位分钟
     */

    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardTime;
    /**
     * 部件工艺标准单价之和，单位元
     */

    @JsonSerialize(using=ToStringSerializer.class)
    private BigDecimal standardPrice;

    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 维护人
     */
    private String updateUser;
    /**
     * 维护时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 发布人
     */
    private String releaseUser;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;
}
