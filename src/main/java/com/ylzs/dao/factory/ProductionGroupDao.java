package com.ylzs.dao.factory;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.factory.ProductionGroup;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 生产组别
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-05-05 09:13:31
 */
@Mapper
public interface ProductionGroupDao extends BaseDAO<ProductionGroup> {


    @Select("SELECT * FROM `production_group` WHERE `production_group_code` = #{producrgourpCode}")
    List<ProductionGroup> getProductCodeByName(String producrgourpCode);

    @Select("SELECT * FROM `production_group` WHERE `production_group_code` = #{producrgourpCode} and craft_category_code=#{craftCategoryCode}")
    ProductionGroup getProductCodeByNameAndCraftCatetory(String producrgourpCode, String craftCategoryCode);

    @MapKey("productionGroupCode")
    public Map<String, ProductionGroup> getAllToMap();

    public List<ProductionGroup> getByName(@Param("productionGroupName") String productionGroupName);

}
