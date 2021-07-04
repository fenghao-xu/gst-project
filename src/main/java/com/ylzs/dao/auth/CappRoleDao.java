package com.ylzs.dao.auth;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.auth.CappRole;
import com.ylzs.entity.auth.resp.CappUserRoleToMenuResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author weikang
 * @Description
 * @Date 2020/5/5
 */
@Mapper
public interface CappRoleDao extends BaseDAO<CappRole> {

    List<CappUserRoleToMenuResp> getRoleByUserCode(String userCode);
}
