package com.ylzs.service.factory;


import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.service.IOriginService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 生产组别
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-05-05 09:13:31
 */
public interface IProductionGroupService extends IOriginService<ProductionGroup> {


    List<ProductionGroup> getProductCodeByName(String producrgourpCode);
    public Map<String,ProductionGroup> getAllToMap();
    ProductionGroup getProductCodeByNameAndCraftCatetory(String producrgourpCode,String craftCategoryCode);

    public List<ProductionGroup> getByName(@Param("productionGroupName") String productionGroupName);
}

