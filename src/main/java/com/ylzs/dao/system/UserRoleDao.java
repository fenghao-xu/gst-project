package com.ylzs.dao.system;

import com.ylzs.entity.system.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户角色
 */
public interface UserRoleDao {
    Integer addUserRoles(@Param("userRoles") List<UserRole> userRoles);
    Integer deleteUserRole(@Param("userCode") String userCode, @Param("roleId") Integer roleId);
    Integer updateUserRole(UserRole userRole);
    List<UserRole> getUserRoleById(@Param("userCode") String userCode, @Param("roleId") Integer roleId);
    List<UserRole> getUserRoleByPage(@Param("keywords") String keywords,
                                     @Param("beginDate") Date beginDate,
                                     @Param("endDate") Date endDate);
}
