package com.ylzs.service.plm.cutParameter.impl;


import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataDosagesDao;
import com.ylzs.entity.plm.CutParameterMasterDataDosages;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataDosagesService;
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
public class CutParameterMasterDataDosagesService implements ICutParameterMasterDataDosagesService {
    @Resource
    private CutParameterMasterDataDosagesDao cutParameterMasterDataDosagesDao;

    @Override
    public int addOrUpdateCutParameterMasterDataDosages(List<CutParameterMasterDataDosages> cutParameterMasterDataDosages) {
        return cutParameterMasterDataDosagesDao.addOrUpdateCutParameterMasterDataDosages(cutParameterMasterDataDosages);
    }
}
