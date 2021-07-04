package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataMarkerTablesDao;
import com.ylzs.entity.plm.CutParameterMasterDataMarkerTables;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataMarketTablesService;
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
public class CutParameterMasterDataMarketTablesService implements ICutParameterMasterDataMarketTablesService {
    @Resource
    private CutParameterMasterDataMarkerTablesDao cutParameterMasterDataMarkerTablesDao;
    @Override
    public int addOrUpdateCutParameterMasterDataMarkerTables(List<CutParameterMasterDataMarkerTables> cutParameterMasterDataMarkerTables) {
        return cutParameterMasterDataMarkerTablesDao.addOrUpdateCutParameterMasterDataMarkerTables(cutParameterMasterDataMarkerTables);
    }
}
