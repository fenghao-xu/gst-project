package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.BigStyleMasterDataWXProceduresDao;
import com.ylzs.entity.plm.BigStyleMasterDataWXProcedures;
import com.ylzs.service.plm.IBigStyleMasterDataWXProceduresService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据
 * @Date: Created in 2020/3/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleMasterDataWXProceduresService implements IBigStyleMasterDataWXProceduresService {

    @Resource
    BigStyleMasterDataWXProceduresDao bigStyleMasterDataWXProceduresDao;


    @Override
    public int addOrUpdateBigStyleDataWXProceduresList(List<BigStyleMasterDataWXProcedures> bigStyleMasterDataWXProcedures) {
        return bigStyleMasterDataWXProceduresDao.addOrUpdateBigStyleDataWXProceduresList(bigStyleMasterDataWXProcedures);
    }
}
