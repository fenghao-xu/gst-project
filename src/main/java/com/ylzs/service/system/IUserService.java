package com.ylzs.service.system;

import com.ylzs.entity.auth.resp.CappUserDataAuthResp;
import com.ylzs.entity.system.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：用户服务接口
 *
 * @author Administrator
 * 2019-09-30 11:53
 */
public interface IUserService {
    Integer addUser(User user);

    Integer deleteUser(String userCode, String userCode1);

    Integer updateUser(User user);

    List<User> getUserByCode(String[] userCodes);

    List<User> getUserAndRole(String[] userCodes);

    List<User> getUserByPage(String keywords, Date beginDate, Date endDate, Integer roleId);

    public List<User> getAllUser();

    public Map<String,User> getUserMap();

    /**
     * 通过userCode获取数据权限
     * @param userCode
     * @return
     */
    CappUserDataAuthResp getDataAuthResp(String userCode);

    List<User> getUserByRoleCode(String[] roleCodes);
}
