package com.ylzs.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.dao.auth.CappUserBrandDao;
import com.ylzs.dao.auth.CappUserClothingCategoryDao;
import com.ylzs.dao.auth.CappUserFactoryDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.dao.system.UserRoleDao;
import com.ylzs.entity.auth.CappUserBrand;
import com.ylzs.entity.auth.CappUserClothingCategory;
import com.ylzs.entity.auth.CappUserFactory;
import com.ylzs.entity.auth.resp.CappUserDataAuthResp;
import com.ylzs.entity.system.User;
import com.ylzs.entity.system.UserRole;
import com.ylzs.service.system.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：用户服务实现
 *
 * @author Administrator
 * 2019-09-30 11:54
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService implements IUserService {
    @Resource
    private UserDao userDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private DictionaryDao dictionaryDao;

    @Resource
    private CappUserBrandDao brandDao;

    @Resource
    private CappUserFactoryDao factoryDao;

    @Resource
    private CappUserClothingCategoryDao categoryDao;

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }

    @Override
    public Integer addUser(User user) {
        Integer ret = userDao.addUser(user);
        if (ret != null && ret >= 1 && user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            String[] split = user.getRoleIds().split(",");
            List<UserRole> userRoles = new ArrayList<UserRole>();
            for (String roleId : split) {
                UserRole userRole = new UserRole();
                userRole.setUserCode(user.getUserCode());
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUpdateTime(new Date());
                userRole.setUpdateUser(user.getUpdateUser());
                userRoles.add(userRole);
            }
            if (!userRoles.isEmpty()) {
                userRoleDao.addUserRoles(userRoles);
            }
        }
        return ret;

    }

    @Override
    public Integer deleteUser(String userCode, String userCode1) {
        String[] userCodes = new String[]{userCode};
        List<User> users = userDao.getUserByCode(userCodes);
        Integer ret = 0;
        if (users != null) {
            for (User user : users) {
                user.setIsInvalid(true);
                user.setUpdateTime(new Date());
                user.setUpdateUser(userCode1);
                ret += userDao.updateUser(user);
            }
        }
        return ret;
    }

    @Override
    public Integer updateUser(User user) {
        Integer ret = userDao.updateUser(user);
        if (user.getRoleIds() != null) {
            if (ret != null && ret >= 1) {
                userRoleDao.deleteUserRole(user.getUserCode(), null);
                if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
                    String[] split = user.getRoleIds().split(",");
                    List<UserRole> userRoles = new ArrayList<UserRole>();
                    for (String roleId : split) {
                        UserRole userRole = new UserRole();
                        userRole.setUserCode(user.getUserCode());
                        userRole.setRoleId(Integer.parseInt(roleId));
                        userRole.setUpdateTime(new Date());
                        userRole.setUpdateUser(user.getUpdateUser());
                        userRoles.add(userRole);
                    }
                    if (!userRoles.isEmpty()) {
                        userRoleDao.addUserRoles(userRoles);
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public List<User> getUserByCode(String[] userCodes) {
        return userDao.getUserByCode(userCodes);
    }

    @Override
    public List<User> getUserAndRole(String[] userCodes) {
        List<User> users = userDao.getUserByCode(userCodes);
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                user.setRoles(dictionaryDao.getRoleByUserCode(user.getUserCode()));
            }
        }
        return users;
    }

    @Override
    public List<User> getUserByPage(String keywords, Date beginDate, Date endDate, Integer roleId) {
        return userDao.getUserByPage(keywords, beginDate, endDate, roleId);
    }

    @Override
    public Map<String, User> getUserMap() {
        return userDao.getUserMap();
    }

    @Override
    public CappUserDataAuthResp getDataAuthResp(String userCode) {
        CappUserDataAuthResp dataAuthResp = new CappUserDataAuthResp();
        //品牌
        QueryWrapper<CappUserBrand> queryBrand = new QueryWrapper<>();
        queryBrand.eq("user_code", userCode);
        List<CappUserBrand> respBrand = brandDao.selectList(queryBrand);
        dataAuthResp.setUserBrands(respBrand);
        //工厂
        QueryWrapper<CappUserFactory> queryFactory = new QueryWrapper<>();
        queryFactory.eq("user_code", userCode);
        List<CappUserFactory> respFactory = factoryDao.selectList(queryFactory);
        dataAuthResp.setUserFactories(respFactory);
        //服装大类
        QueryWrapper<CappUserClothingCategory> queryClothingCategory = new QueryWrapper<>();
        queryClothingCategory.eq("user_code", userCode);
        List<CappUserClothingCategory> respClothingCategory = categoryDao.selectList(queryClothingCategory);
        dataAuthResp.setUserClothingCategories(respClothingCategory);

        return dataAuthResp;
    }

    @Override
    public List<User> getUserByRoleCode(String[] roleCodes) {
        return userDao.getUserByRoleCode(roleCodes);
    }

}
