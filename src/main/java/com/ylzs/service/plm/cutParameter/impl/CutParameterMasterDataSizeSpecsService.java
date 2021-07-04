package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataSizesSpecsDao;
import com.ylzs.entity.plm.CutParameterMasterDataSizesSpecs;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataSizeSpecsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
@Transactional(rollbackFor = Exception.class)
public class CutParameterMasterDataSizeSpecsService implements ICutParameterMasterDataSizeSpecsService {
    @Resource
    private CutParameterMasterDataSizesSpecsDao cutParameterMasterDataSizesSpecsDao;

    @Override
    public int addOrUpdateCutParameterMasterDataSpecs(List<CutParameterMasterDataSizesSpecs> cutParameterMasterDataSizesSpecs) {
        return cutParameterMasterDataSizesSpecsDao.addOrUpdateCutParameterMasterDataSpecs(cutParameterMasterDataSizesSpecs);
    }
}
