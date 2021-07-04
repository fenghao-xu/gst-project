package com.ylzs.service.system;

import com.ylzs.entity.system.RoleAuth;

import java.util.Date;
import java.util.List;

/**
 * 说明：角色权限服务接口
 *
 * @author Administrator
 * 2019-09-30 13:43
 */
public interface IRoleAuthService {
    Integer addRoleAuth(RoleAuth roleAuth);
    Integer deleteRoleAuth(Integer userId, Integer moduleId);
    Integer updateRoleAuth(RoleAuth roleAuth);
    List<RoleAuth> getRoleAuthById(Integer userId, Integer moduleId);
    List<RoleAuth> getRoleAuthByPage(String keywords, Date beginDate, Date endDate, Integer roleId);
    List<RoleAuth> getAllByRoleId(Integer roleId);
    List<RoleAuth> getRoleByUserCode(String userCode);
}
