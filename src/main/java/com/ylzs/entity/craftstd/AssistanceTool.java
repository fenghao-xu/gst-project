package com.ylzs.entity.craftstd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 辅助工具实体类
 */
@Data
public class AssistanceTool  extends SuperEntity {
    /**
     * 工具代码
     */
    private String toolCode;


    /**
     * 工具类型
     */
    private String toolType;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 工具名称(英文)
     */
    private String toolNameEng;

    /**
     * 备注
     */
    private String remark;


}