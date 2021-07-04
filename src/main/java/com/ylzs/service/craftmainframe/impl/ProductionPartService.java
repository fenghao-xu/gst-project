package com.ylzs.service.craftmainframe.impl;

import com.ylzs.dao.craftmainframe.FlowNumConfigDao;
import com.ylzs.dao.craftmainframe.ProductionPartDao;
import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.service.craftmainframe.IProductionPartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProductionPartService implements IProductionPartService {

    @Resource
    private ProductionPartDao productionPartDao;
    @Resource
    private FlowNumConfigDao flowNumConfigDao;

    @Override
    public int deleteByPrimaryKey(Long randomCode) {
        return productionPartDao.deleteByPrimaryKey(randomCode);
    }

    @Override
    public int insert(ProductionPart record) {
        return productionPartDao.insert(record);
    }

    @Override
    public int insertSelective(ProductionPart record) {
        return productionPartDao.insertSelective(record);
    }

    @Override
    public Map<String, ProductionPart> getMapByMainFrameCode(String mainFrameCode) {
        return productionPartDao.getMapByMainFrameCode(mainFrameCode);
    }



    @Override
    public List<FlowNumConfig> getFlowNumConfigAll() {
        return flowNumConfigDao.getFlowNumConfigAll();
    }

    @Override
    public ProductionPart selectByPrimaryKey(Long randomCode) {
        return productionPartDao.selectByPrimaryKey(randomCode);
    }

    @Override
    public int updateByPrimaryKeySelective(ProductionPart record) {
        return productionPartDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ProductionPart record) {
        return productionPartDao.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<ProductionPart> list) {
        return productionPartDao.batchInsert(list);
    }

    @Override
    public List<ProductionPart> getByMainFrameRandomCode(Long mainFrameRandomCode) {
        return productionPartDao.getByMainFrameRandomCode(mainFrameRandomCode);
    }

    @Override
    public List<ProductionPart> getByCondition(Long craftCategoryRandomCode, Long mainFrameRandomCode, String keywords, Date beginDate, Date endDate) {
        return productionPartDao.getByCondition(craftCategoryRandomCode, mainFrameRandomCode, keywords, beginDate, endDate);
    }

}


