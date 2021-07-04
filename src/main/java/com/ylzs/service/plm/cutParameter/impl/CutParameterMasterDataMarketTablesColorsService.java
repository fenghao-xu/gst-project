package com.ylzs.service.plm.cutParameter.impl;

import com.ylzs.dao.plm.cutParameter.CutParameterMasterDataMarkerTablesColorsDao;
import com.ylzs.entity.plm.CutParameterMasterDataMarkerTablesColors;
import com.ylzs.service.plm.cutParameter.ICutParameterMasterDataMarketTablesColorsService;
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
public class CutParameterMasterDataMarketTablesColorsService implements ICutParameterMasterDataMarketTablesColorsService {
    @Resource
    private CutParameterMasterDataMarkerTablesColorsDao cutParameterMasterDataMarkerTablesColorsDao;
    @Override
    public int addOrUpdateCutParameterMasterDataMarkerTablesColors(List<CutParameterMasterDataMarkerTablesColors> cutParameterMasterDataMarkerTablesColors) {
        return cutParameterMasterDataMarkerTablesColorsDao.addOrUpdateCutParameterMasterDataMarkerTablesColors(cutParameterMasterDataMarkerTablesColors);
    }
}
