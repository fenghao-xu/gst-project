package com.ylzs.dao.system;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.system.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-26 11:24
 */
public interface UserDao extends BaseDAO<User> {
    Integer addUser(User user);

    Integer deleteUser(String userCode);

    public List<User> getAllUser();

    @MapKey("userCode")
    public Map<String,User> getUserMap();

    Integer updateUser(User user);

    List<User> getUserByCode(@Param("userCodes") String[] userCodes);

    List<User> getUserByPage(@Param("keywords") String keywords,
                             @Param("beginDate") Date beginDate,
                             @Param("endDate") Date endDate,
                             @Param("roleId") Integer roleId);

     List<User> getUserByRoleCode(@Param("roleCodes") String[] roleCodes);

}