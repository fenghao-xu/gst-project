package com.ylzs.entity.sewingcraft;

import com.ylzs.core.model.SuperEntity;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-02-25 17:00
 * 车缝工序词库动作
 */
@Data
public class SewingCraftAction  {
    /**
     * 工序关联代码--工序词库sewing_craft_warehouse的random_code
     */
    private Long sewingCraftRandomCode;
    /**
     * 序号
     */
    private Integer orderNum;
    /**
     * 动作代码
     */
    private String motionCode;
    /**
     * 存储前端传过来的颜色标识
     */
    private String mark;


    /**
     * 动作描述
     */
    private String motionName;
    /**
     * 频率
     */
    private BigDecimal frequency;
    /**
     * 动作描述
     */
    private String description;
    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 转速
     */
    private Integer speed;
    /**
     * 机器时间
     */
    private Integer machineTime;
    /**
     * 人工时间
     */
    private Integer manualTime;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;

    /**
     * 机器时间----频率等于1的
     */
    private Integer machineTimeBase;
    /**
     * 人工时间--频率等于1的
     */
    private Integer manualTimeBase;

    private Long id;

}
