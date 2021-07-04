package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 工艺品类与服装品类关联表
 */
@Data
public class CategoryRelation {
    /**
     * 唯一id
     */
    private Integer id;
    /**
     * 工艺品类id
     */
    private Integer craftCategoryId;
    /**
     * 服装品类id
     */
    private Integer clothesCategoryId;
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
     * 工艺品类代码
     */
    private String craftCategoryCode;
    /**
     * 服装品类代码
     */
    private String clothesCategoryCode;
}
