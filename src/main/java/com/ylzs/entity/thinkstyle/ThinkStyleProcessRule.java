package com.ylzs.entity.thinkstyle;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;
/**
 * @description: 智库款工艺处理规则
 * @author: lyq
 * @date: 2020-03-05 11:55
 */

@Data
public class ThinkStyleProcessRule extends SuperEntity {

    /**
     * 智库款关联代码
     */
    private Long styleRandomCode;

    /**
     * 上级部件关联代码
     */
    private Long partRandomCode;

    /**
     * 处理方式
     */
    private Byte processType;

    /**
     * 原工序代码列表
     */
    private String sourceCraftCode;

    /**
     * 原工序代码名称列表
     */
    private String sourceCraftName;

    /**
     * 原工序关联代码列表
     */
    private String sourceCraftRandomCode;


    /**
     * 新工序代码列表
     */
    private String actionCraftCode;

    /**
     * 新工序代码名称列表
     */
    private String actionCraftName;

    /**
     * 新工序关联代码列表
     */
    private String actionCraftRandomCode;

    private Integer processingSortNum;


}

