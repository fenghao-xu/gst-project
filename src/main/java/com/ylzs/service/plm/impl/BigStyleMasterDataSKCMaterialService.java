package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.BigStyleMasterDataSKCMaterialDao;
import com.ylzs.entity.plm.BigStyleMasterDataSKCMaterial;
import com.ylzs.service.plm.IBigStyleMasterDataSKCMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据 SKC子表-Material子表
 * @Date: Created in 2020/3/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleMasterDataSKCMaterialService implements IBigStyleMasterDataSKCMaterialService {

    @Resource
    BigStyleMasterDataSKCMaterialDao bigStyleMasterDataSKCMaterialDao;


    @Override
    public int addOrUpdateBigStyleDataSKCMaterialList(List<BigStyleMasterDataSKCMaterial> bigStyleMasterDataSKCMaterial) {
        return bigStyleMasterDataSKCMaterialDao.addOrUpdateBigStyleDataSKCMaterialList(bigStyleMasterDataSKCMaterial);
    }
}
