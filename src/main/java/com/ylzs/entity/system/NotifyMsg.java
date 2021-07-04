package com.ylzs.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 通知消息表
 */
@Data
public class NotifyMsg {
    /**
     * 唯一标识id
     */
    private Long id;
    /**
     * 消息类型 1提交 2拒绝
     */
    private Byte msgType;
    /**
     * 关键id
     */
    private Long msgKeyId;
    /**
     * 关键代码
     */
    private String msgKeyCode;
    /**
     * 消息
     */
    private String msgTxt;
    /**
     * 是否已读
     */
    private Boolean isRead;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 读取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 读取用户
     */
    private String readUser;
    /**
     * 备注
     */
    private String remark;


}
