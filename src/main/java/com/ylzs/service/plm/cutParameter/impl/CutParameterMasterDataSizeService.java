package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataSizesDao;
import com.ylzs.entity.plm.CutParameterMasterDataSizes;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataSizeService;
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
public class CutParameterMasterDataSizeService implements ICutParameterMasterDataSizeService {
    @Resource
    private CutParameterMasterDataSizesDao cutParameterMasterDataSizesDao;
    @Override
    public int addOrUpdateCutParameterMasterDataSizes(List<CutParameterMasterDataSizes> cutParameterMasterDataSizes) {
        return cutParameterMasterDataSizesDao.addOrUpdateCutParameterMasterDataSizes(cutParameterMasterDataSizes);
    }
}
