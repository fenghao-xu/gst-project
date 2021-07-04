package com.ylzs.dao.system;

import com.ylzs.entity.system.RoleAuth;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：角色权限
 *
 * @author lyq
 * 2019-09-26 11:52
 */
public interface RoleAuthDao {
    Integer addRoleAuth(RoleAuth roleAuth);
    Integer deleteRoleAuth(@Param("roleId") Integer roleId, @Param("moduleId") Integer moduleId);
    Integer updateRoleAuth(RoleAuth roleAuth);
    List<RoleAuth> getRoleAuthById(@Param("roleId") Integer roleId, @Param("moduleId") Integer moduleId);
    List<RoleAuth> getRoleAuthByPage(@Param("keywords") String keywords,
                                     @Param("beginDate") Date beginDate,
                                     @Param("endDate") Date endDate,
                                     @Param("roleId") Integer roleId);
    List<RoleAuth> getAllByRoleId(Integer roleId);
    List<RoleAuth> getRoleByUserCode(@Param("userCode") String userCode);
}
