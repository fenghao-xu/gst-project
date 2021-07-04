package com.ylzs.service.materialCraft.impl;


import com.ylzs.dao.materialCraft.MaterialCraftPropertyDao;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.materialCraft.IMaterialCraftPropertyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description 材料属性
 * @Date 2020/3/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialCraftPropertyServiceImpl extends OriginServiceImpl<MaterialCraftPropertyDao, MaterialCraftProperty> implements IMaterialCraftPropertyService {

    @Resource
    private MaterialCraftPropertyDao propertyDao;

    @Override
    public int updatePublishStatus(List<Long> list) {
        return propertyDao.updatePublishStatus(list);
    }

    @Override
    public int updateNotActiveStatus(List<Long> list) {
        return propertyDao.updateNotActiveStatus(list);
    }
}
