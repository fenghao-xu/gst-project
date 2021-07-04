package com.ylzs.service.auth.impl;

import com.ylzs.dao.auth.CappUserBrandDao;
import com.ylzs.entity.auth.CappUserBrand;
import com.ylzs.service.auth.CappUserBrandService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author weikang
 * @Description 用户品牌权限接口实现类
 * @Date 2020/5/5
 */
@Service
public class CappUserBrandServiceImpl extends OriginServiceImpl<CappUserBrandDao, CappUserBrand> implements CappUserBrandService {

}
