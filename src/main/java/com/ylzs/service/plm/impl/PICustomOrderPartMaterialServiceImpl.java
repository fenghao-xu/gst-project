package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.PICustomOrderPartMaterialDao;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:33 2020/3/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PICustomOrderPartMaterialServiceImpl implements IPICustomOrderPartMaterialService {


    @Resource
    private PICustomOrderPartMaterialDao piCustomOrderPartMaterialDao;

    @Override
    public void addCustomOrderPartMaterialList(List<PICustomOrderPartMaterial> piCustomOrderPartMaterials) {
        piCustomOrderPartMaterialDao.addCustomOrderPartMaterialList(piCustomOrderPartMaterials);
    }

    @Override
    public List<PICustomOrderPartMaterial> getOrderAll(String orderId, String orderLineId) {
        return piCustomOrderPartMaterialDao.getOrderAll(orderId,orderLineId);
    }

    @Override
    public PICustomOrderPartMaterial getMainMaterialData(String orderId, String orderLineId, String materialCode) {
        PICustomOrderPartMaterial mainfabric = null;
        try {
            mainfabric = piCustomOrderPartMaterialDao.getMainMaterialData(orderId,orderLineId,materialCode);
        }catch (Exception ex){
            ex.printStackTrace();
            mainfabric = null;
        }
        return mainfabric;
    }
}
