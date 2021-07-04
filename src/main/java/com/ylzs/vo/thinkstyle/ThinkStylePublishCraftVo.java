package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;

/**
 * 智库款发布信息明细
 */

@Data
public class ThinkStylePublishCraftVo implements Serializable {
    private static final long serialVersionUID = 7751400091843370117L;

    /**
     * 部件代码
     */
    private String bjdm;
    /**
     * 序号
     */
    private String xh;
    /**
     * 工序代码
     */
    private String gxdm;
    /**
     * 工序名称
     */
    private String gxmc;
    /**
     * 机器类型名称
     */
    private String jqdm;
    /**
     * 辅助工具名称
     */
    private String gjmc;
    /**
     * 是否外协
     */
    private String wxbz;
    /**
     * 品质说明
     */
    private String pzsm;
    /**
     * 工艺要求
     */
    private String zgsm;
    /**
     * 标准时间
     */
    private String SMV;
    /**
     * 单价
     */
    private String dj;


}
