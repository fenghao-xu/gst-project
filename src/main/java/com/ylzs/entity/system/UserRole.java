package com.ylzs.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户角色表
 */
@Data
public class UserRole {
    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 更新用户
     */
    private String updateUser;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
