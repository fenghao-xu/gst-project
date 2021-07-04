package com.ylzs.service.auth;


import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 用户对应角色接口
 * @Date 2020/5/5
 */
public interface CappRoleService extends IOriginService<CappRole>  {

    /**
     * 通过userCode获取角色数据
     * @param userCode
     * @return
     */
    List<CappUserRoleToMenuResp> getRoleByUserCode(String userCode);
}
