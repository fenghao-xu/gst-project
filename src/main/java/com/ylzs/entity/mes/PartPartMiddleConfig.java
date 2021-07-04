package com.ylzs.entity.mes;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 部件对应部件中类表
 */
@Data
@TableName(value = "part_part_middle_config")
public class PartPartMiddleConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结构部件编码
     */
    @TableField(value = "part_code")
    private String partCode;

    /**
     * 工艺品类编码
     */
    @TableField(value = "craft_category_code")
    private String craftCategoryCode;

    /**
     * 服装品类编码
     */
    @TableField(value = "clothes_category_code")
    private String clothesCategoryCode;

    /**
     * 部件中类编码
     */
    @TableField(value = "part_middle_code")
    private String partMiddleCode;

    @TableField(value = "remark")
    private String remark;

    /**
     * 更新人
     */
    @TableField(value = "update_user")
    private String updateUser;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}