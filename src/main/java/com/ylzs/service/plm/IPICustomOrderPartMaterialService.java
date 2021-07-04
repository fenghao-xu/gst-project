package com.ylzs.service.plm;

import com.ylzs.entity.plm.PICustomOrderPartMaterial;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:33 2020/3/19
 */
public interface IPICustomOrderPartMaterialService {


    void addCustomOrderPartMaterialList(List<PICustomOrderPartMaterial> piCustomOrderPartMaterials);

    List<PICustomOrderPartMaterial> getOrderAll(String orderId,String orderLineId);

    PICustomOrderPartMaterial getMainMaterialData(String orderId,String orderLineId,String materialCode);
}
