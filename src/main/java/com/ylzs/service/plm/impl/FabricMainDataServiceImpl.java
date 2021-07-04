package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.FabricMainDataDao;
import com.ylzs.entity.plm.FabricMainData;
import com.ylzs.service.plm.IFabricMainDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:24 2020/3/11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricMainDataServiceImpl implements IFabricMainDataService {
    @Resource
    private FabricMainDataDao fabricMainDataDao;

    @Override
    public void addOrUpdateFabricDataDao(FabricMainData fabricMainData) {
        fabricMainDataDao.addOrUpdateFabricDataDao(fabricMainData);
    }

    @Override
    public List<FabricMainData> getAllFabricData() {
        return fabricMainDataDao.getAllFabricData();
    }

    @Override
    public List<FabricMainData> getFabricMainDataByCode(String mainMaterialCode) {
        return fabricMainDataDao.getFabricMainDataByCode(mainMaterialCode);
    }
}
