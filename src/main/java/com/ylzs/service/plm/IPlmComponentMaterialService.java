package com.ylzs.service.plm;

import com.ylzs.entity.plm.PlmComponentMaterial;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 9:50 2020/3/16
 */
public interface IPlmComponentMaterialService {
    void addPlmComponentMaterial (List<PlmComponentMaterial> PlmComponentMaterialList);

    void delPlmComponentMaterial (String styleCode);

    List<PlmComponentMaterial> getAll();
}
