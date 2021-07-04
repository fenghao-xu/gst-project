package com.ylzs.entity.craftstd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ylzs.core.model.SuperEntity;
import java.util.Date;
import lombok.Data;

/**
 * 工艺标准与辅助工具关联表
 */
@Data
public class CraftStdTool extends SuperEntity {
    /**
     * 工序标准id
     */
    private Long craftStdId;

    /**
     * 工具代码
     */
    private String toolCode;

    /**
     * 工具名称
     */
    private String toolName;
}