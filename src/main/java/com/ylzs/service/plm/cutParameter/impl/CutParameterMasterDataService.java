package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataDao;
import com.ylzs.entity.plm.CutParameterMasterData;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class CutParameterMasterDataService implements ICutParameterMasterDataService {
    @Resource
    private CutParameterMasterDataDao cutParameterMasterDataDao;
    @Override
    public int addOrUpdateCutParameterMasterData(CutParameterMasterData cutParameterMasterData) {
        return cutParameterMasterDataDao.addOrUpdateCutParameterMasterData(cutParameterMasterData);
    }
}
