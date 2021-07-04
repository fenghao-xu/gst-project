package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:12 2020/3/19
 */
public interface PICustomOrderPartMaterialDao extends BaseDAO<PICustomOrderPartMaterial> {


    void addCustomOrderPartMaterialList(@Param("piCustomOrderPartMaterials") List<PICustomOrderPartMaterial> piCustomOrderPartMaterials);

    List<PICustomOrderPartMaterial> getOrderAll(@Param("orderId") String orderId, @Param("orderLineId") String orderLineId);


    PICustomOrderPartMaterial getMainMaterialData(String orderId,String orderLineId,String materialCode);



}
