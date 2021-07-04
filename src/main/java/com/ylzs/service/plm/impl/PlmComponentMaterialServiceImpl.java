package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.PlmComponentMaterialDao;
import com.ylzs.entity.plm.PlmComponentMaterial;
import com.ylzs.service.plm.IPlmComponentMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 9:51 2020/3/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlmComponentMaterialServiceImpl implements IPlmComponentMaterialService {
    private PlmComponentMaterialDao plmComponentMaterialDao;

    @Override
    public void addPlmComponentMaterial(List<PlmComponentMaterial> PlmComponentMaterialList) {
        plmComponentMaterialDao.addPlmComponentMaterial(PlmComponentMaterialList);
    }

    @Override
    public void delPlmComponentMaterial(String styleCode) {
        plmComponentMaterialDao.delPlmComponentMaterial(styleCode);
    }

    @Override
    public List<PlmComponentMaterial> getAll() {
        return plmComponentMaterialDao.getAll();
    }
}
