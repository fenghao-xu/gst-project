package com.ylzs.entity.craftstd;

import lombok.Data;

/**
 * 工艺标准统计
 */
@Data
public class CraftStdStatistic {
    /**
     * 提交数量
     */
    private transient Long commitCount;
    /**
     * 审核数理
     */
    private transient Long auditCount;
    /**
     * 提取数量
     */
    private transient Long extractCount;
    /**
     * 缺少图片数量
     */
    private transient Long lackPicCount;
    /**
     * 缺少视频数量
     */
    private transient Long lackVideoCount;
}
