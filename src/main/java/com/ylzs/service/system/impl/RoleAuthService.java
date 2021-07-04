package com.ylzs.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.RedisUtil;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.dao.auth.CappRoleDao;
import com.ylzs.dao.system.RoleAuthDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import com.ylzs.entity.system.RoleAuth;
import com.ylzs.entity.system.User;
import com.ylzs.service.auth.CappRoleService;
import com.ylzs.service.system.IRoleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 说明：角色权限服务实现
 *
 * @author Administrator
 * 2019-09-30 13:44
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleAuthService implements IRoleAuthService {
    @Resource
    private RoleAuthDao roleAuthDao;
    @Resource
    private UserDao userDao;

    @Autowired
    private RedisUtil redisUtil;
    
    @Resource
    private CappRoleDao roleDao;

    /**
     * 刷新缓存的用户权限
     */
    private void updateCacheRoleAuth() {
        Map<Object, Object> map = redisUtil.hmget(RedisContants.REDIS_LOGIN_KEY_PRE);
        if (null != map && map.size() > 0) {
            for (Object uc : map.values()) {
                UserContext userContext = JSONObject.parseObject((String) uc, UserContext.class);
                List<User> users = userDao.getUserByCode(new String[]{userContext.getUserCode()});
                if (users != null && users.size() > 0) {
                    User user = users.get(0);
//                    List<RoleAuth> roleAuths = roleAuthDao.getRoleByUserCode(user.getUserCode());
                    List<CappUserRoleToMenuResp> roleAuths = roleDao.getRoleByUserCode(user.getUserCode());
                    userContext.setRoleAuthList(roleAuths);
                }
            }
        }
       /* for (Object uc: StaticDataCache.TOKEN_MAP.values()) {
            UserContext userContext = (UserContext) uc;
            List<User> users = userDao.getUserByCode(new String[]{userContext.getUserCode()});
            if (users != null && users.size() > 0) {
                User user = users.get(0);
                List<RoleAuth> roleAuths = roleAuthDao.getRoleByUserCode(user.getUserCode());
                userContext.setRoleAuthList(roleAuths);
            }
        }*/
    }

    @Override
    public Integer addRoleAuth(RoleAuth roleAuth) {
        Integer ret = roleAuthDao.addRoleAuth(roleAuth);
        updateCacheRoleAuth();
        return ret;
    }

    @Override
    public Integer deleteRoleAuth(Integer roleId, Integer moduleId) {
        Integer ret = roleAuthDao.deleteRoleAuth(roleId, moduleId);
        updateCacheRoleAuth();
        return ret;
    }

    @Override
    public Integer updateRoleAuth(RoleAuth roleAuth) {
        Integer ret = roleAuthDao.updateRoleAuth(roleAuth);
        updateCacheRoleAuth();
        return ret;
    }

    @Override
    public List<RoleAuth> getRoleAuthById(Integer roleId, Integer moduleId) {
        return roleAuthDao.getRoleAuthById(roleId, moduleId);
    }

    @Override
    public List<RoleAuth> getRoleAuthByPage(String keywords, Date beginDate, Date endDate, Integer roleId) {
        return roleAuthDao.getRoleAuthByPage(keywords, beginDate, endDate, roleId);
    }

    @Override
    public List<RoleAuth> getAllByRoleId(Integer roleId) {
        return roleAuthDao.getAllByRoleId(roleId);
    }

    @Override
    public List<RoleAuth> getRoleByUserCode(String userCode) {
        return roleAuthDao.getRoleByUserCode(userCode);
    }

}
