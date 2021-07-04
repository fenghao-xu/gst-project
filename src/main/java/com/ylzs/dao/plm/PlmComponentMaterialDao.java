package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PlmComponentMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:41 2020/3/13
 */
@Mapper
public interface PlmComponentMaterialDao  extends BaseDAO<PlmComponentMaterial> {
    void addPlmComponentMaterial (List<PlmComponentMaterial> PlmComponentMaterialList);

    List<PlmComponentMaterial> getAll();

    void delPlmComponentMaterial (@Param("styleCode") String styleCode);

    List<PlmComponentMaterial> findComponentMaterialByCode(@Param("code") String code);

}
