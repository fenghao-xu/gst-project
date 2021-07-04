package com.ylzs.service.auth.impl;

import com.ylzs.dao.auth.CappRoleDao;
import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import com.ylzs.service.auth.CappRoleService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description 用户对应角色接口实现类
 * @Date 2020/5/5
 */
@Service
public class CappRoleServiceImpl extends OriginServiceImpl<CappRoleDao, CappRole> implements CappRoleService {

    @Resource
    private CappRoleDao roleDao;

    @Override
    public List<CappUserRoleToMenuResp> getRoleByUserCode(String userCode) {
        return roleDao.getRoleByUserCode(userCode);
    }
}
