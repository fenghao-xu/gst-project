package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：lyq
 * @description：智库款工艺处理规则
 * @date ：2020-03-07 16:47
 */

@Data
public class ThinkStyleProcessRuleVo implements Serializable {
    private static final long serialVersionUID = 6526206436493868442L;

    /**
     * 智库款工艺处理规则关联代码
     */
    Long randomCode;

    /**
     * 处理类型
     */
    Byte processType;
    /**
     * 处理类型名称
     */
    String processTypeName;

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
