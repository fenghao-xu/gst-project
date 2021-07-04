package com.ylzs.service.factory.impl;

import com.ylzs.dao.factory.ProductionGroupDao;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service()
public class ProductionGroupServiceImpl extends OriginServiceImpl<ProductionGroupDao, ProductionGroup> implements IProductionGroupService {

    @Autowired
    private ProductionGroupDao productionGroupDao;

    @Override
    public List<ProductionGroup> getProductCodeByName(String producrgourpCode) {
        return productionGroupDao.getProductCodeByName(producrgourpCode);
    }

    @Override
    public Map<String, ProductionGroup> getAllToMap() {
        return productionGroupDao.getAllToMap();
    }

    @Override
    public ProductionGroup getProductCodeByNameAndCraftCatetory(String producrgourpCode, String craftCategoryCode) {
        return productionGroupDao.getProductCodeByNameAndCraftCatetory(producrgourpCode, craftCategoryCode);
    }

    @Override
    public List<ProductionGroup> getByName(String productionGroupName) {
        return productionGroupDao.getByName(productionGroupName);
    }
}