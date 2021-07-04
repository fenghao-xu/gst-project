package com.ylzs.entity.auth.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description
 * @Date 2020/5/14
 */
@Data
public class CappUserRoleToMenuResp implements Serializable {
    private static final long serialVersionUID = -4823481801630857555L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 菜单名称
     */
    private String menuName;

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
}
