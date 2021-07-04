package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataMarketInfoDao;
import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataMarketInfoHemsDao;
import com.ylzs.entity.plm.CutParameterMasterDataMarkInfo;
import com.ylzs.entity.plm.CutParameterMasterDataMarkInfoHems;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataMarkerInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class CutParameterMasterDataMarkerInfoHemsService implements ICutParameterMasterDataMarkerInfoHemsService{
    @Resource
    private CutParameterMasterDataMarketInfoHemsDao cutParameterMasterDataMarketInfoHemsDao;

    @Override
    public void addOrUpdateCutParameterMasterDataMarketInfoHems(List<CutParameterMasterDataMarkInfoHems> cutParameterMasterDataMarkInfoHems) {
         cutParameterMasterDataMarketInfoHemsDao.addOrUpdateCutParameterMasterDataMarketInfoHems(cutParameterMasterDataMarkInfoHems);
    }
}
