package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：工艺部件
 *
 * @author lyq
 * 2019-09-24 10:06
 */
@Data
public class CraftPart {
    /**
     * 自增id
     */
    private Integer id;
    /**
     * 工艺品类编码
     */
    private Integer craftCategoryId;
    /**
     * 工艺部件代码
     */
    private String craftPartCode;
    /**
     * 工艺部件名称
     */
    private String craftPartName;
    /**
     * 工段代码
     */
    private String sectionId;
    /**
     * 父工艺部件代码
     */
    private String parentCraftPartId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 失效标志
     */
    @JsonIgnore
    private Boolean isInvalid;
    /**
     * 维护用户
     */
    private String updateUser;

    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     * 工艺品类代码
     */
    private String craftCategoryCode;

    /**
     * 工段代码
     */
    private transient String sectionCode;
    /**
     * 工段名称
     */
    private transient String sectionName;

    /**
     * 工艺品类和部件名
     */
    private transient String craftCategoryPartName;
}
