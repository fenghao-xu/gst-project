package com.ylzs.entity.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weikang
 * @Description 菜单实体
 * @Date 2020/5/5
 */
@Data
public class CappMenu implements Serializable {
    private static final long serialVersionUID = -8429898236394170910L;

    private Integer id;

    /**
     * 菜单code
     */
    private String menuCode;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单code
     */
    private String parentMenuCode;

    /**
     * 父菜单名称
     */
    private String parentMenuName;

    /**
     * 菜单地址
     */
    private String menuUrl;

    /**
     * 菜单等级
     */
    private Integer menuLevel;

    /**
     * 菜单位置
     */
    private Integer menuIndex;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String  updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
