package com.ylzs.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ylzs.entity.craftstd.Dictionary;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


/**
 * 用户表
 */
@Data
public class User {
    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 是否是管理员
     */
    private Boolean isAdmin;
    /**
     * 所属部门
     */
    private String department;
    /**
     * 失效标志
     */
    @JsonIgnore
    private Boolean isInvalid;
    /**
     * 维护用户
     */
    private String updateUser;
    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 密码（MD5)
     */
    @JsonIgnore
    private String pwd;


    /**
     * 用户角色列表
     */
    private transient List<Dictionary> roles;

    /**
     * 用户角色id
     */
    private transient String roleIds;

    /**
     * 用户输入的密码
     */
    private transient String password;

    /**
     * 角色名称列表
     */
    private transient String roleNames;



}
