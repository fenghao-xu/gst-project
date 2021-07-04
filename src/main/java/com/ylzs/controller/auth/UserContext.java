package com.ylzs.controller.auth;

import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import com.ylzs.entity.system.RoleAuth;
import lombok.Data;

import java.util.List;

/**
 * 说明：用户上下文
 *
 * @author lyq
 * 2019-10-15 15:36
 */
@Data
public class UserContext {
    public UserContext(String userCode, String userName, List<CappUserRoleToMenuResp> roleAuthList, String token, boolean isAdmin, String ip) {
        super();
        this.userCode = userCode;
        this.userName = userName;
        this.roleAuthList = roleAuthList;
        this.token = token;
        this.isAdmin = isAdmin;
        this.ip = ip;

    }

    /**
     * 用户代码
     */
    private String userCode;

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 模块角色权限
     */
    private List<CappUserRoleToMenuResp> roleAuthList;

    /**
     * 令牌
     */
    private String token;
    /**
     * 是否是管理员
     */
    private boolean isAdmin;
    /**
     * ip地址
     */
    private String ip;


    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public List<CappUserRoleToMenuResp> getRoleAuthList() {
        return roleAuthList;
    }
    public void setRoleAuthList(List<CappUserRoleToMenuResp> roleAuthList) {
        this.roleAuthList = roleAuthList;
    }
    public String getToken() {return token; }
    public void setToken(String token) {this.token = token;}
    public boolean getIsAdmin() {return isAdmin;}
    public void setIsAdmin(boolean isAdmin) {this.isAdmin = isAdmin;}
    public String getIp() {return ip;}
    public void setIp(String ip) {this.ip = ip;}
    public String getUser() { return  this.userCode + " " + this.userName;}
}
