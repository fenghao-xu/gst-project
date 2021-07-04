package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.BigStyleMasterDataSKCDao;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.service.plm.IBigStyleMasterDataSKCService;
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
public class BigStyleMasterDataSKCService implements IBigStyleMasterDataSKCService {

    @Resource
    BigStyleMasterDataSKCDao bigStyleMasterDataSKCDao;


    @Override
    public int addOrUpdateBigStyleDataSKCList(List<BigStyleMasterDataSKC> bigStyleMasterDataSKC) {
        return bigStyleMasterDataSKCDao.addOrUpdateBigStyleDataSKCList(bigStyleMasterDataSKC);
    }
}
