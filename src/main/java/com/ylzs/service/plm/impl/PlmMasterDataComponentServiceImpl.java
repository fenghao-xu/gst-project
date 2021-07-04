package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.PlmMasterDataComponentDao;
import com.ylzs.entity.plm.PlmMasterDataComponent;
import com.ylzs.service.plm.IPlmMasterDataComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 14:02 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlmMasterDataComponentServiceImpl implements IPlmMasterDataComponentService {
    @Autowired
    private PlmMasterDataComponentDao plmMasterDataComponentDao;

    @Override
    public void addPlmMasterDataComponent(List<PlmMasterDataComponent> plmMasterDataComponentList) {
        plmMasterDataComponentDao.addPlmMasterDataComponent(plmMasterDataComponentList);
    }

    @Override
    public void delPlmMasterDataComponent(String styleCode) {
        plmMasterDataComponentDao.delPlmMasterDataComponent(styleCode);
    }


    @Override
    public List<PlmMasterDataComponent> getAll() {
        return plmMasterDataComponentDao.getAll();
    }


}
