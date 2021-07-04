package com.ylzs.entity.craftstd;

import lombok.Data;

import java.util.Date;

/**
 * 工序部件关联表
 */
@Data
public class CraftStdPart {
    /**
     * 工序标准id
     */
    private Long craftStdId;
    /**
     * 工艺部件id
     */
    private Integer craftPartId;
    /**
     * 工艺品类id
     */
    private Integer craftCategoryId;
    /**
     * 维护人
     */
    private String updateUser;
    /**
     * 维护时间
     */
    private Date updateTime;
}
