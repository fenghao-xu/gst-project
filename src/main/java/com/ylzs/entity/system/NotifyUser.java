package com.ylzs.entity.system;

import lombok.Data;

import java.util.Date;

/**
 * 用户消息关联表
 */
@Data
public class NotifyUser {
    /**
     * 唯一标识
     */
    private Integer id;
    /**
     * 工艺品类id
     */
    private Integer craftCategoryId;
    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 消息类型
     */
    private Byte msgType;
    /**
     * 维护用户
     */
    private String updateUser;
    /**
     * 维护时间
     */
    private Date updateTime;

}
