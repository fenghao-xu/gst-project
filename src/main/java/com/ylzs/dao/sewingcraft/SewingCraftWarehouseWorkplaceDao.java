package com.ylzs.dao.sewingcraft;

import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import com.ylzs.vo.SewingCraftVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-07 15:10
 */
public interface SewingCraftWarehouseWorkplaceDao {
    public void addData(Map<String, Object> map);

    public void updateData(Map<String, Object> map);

    List<SewingCraftVo> searchSewingCraftData(HashMap params);

    public List<String> getWorkplaceBysewingRandomCode(Long param);

    public List<SewingCraftWarehouseWorkplace> getDataBySewingCraftRandomCode(Long randomCode);

    public List<SewingCraftWarehouseWorkplace> getDataByParam(Map<String, Object> map);

    @MapKey("craftCode")
    Map<String,SewingCraftWarehouseWorkplace> getMapByCraftCode(Map<String, Object> map);


    public void deleteWorkplaceBysewingRandomCode(Long randomCode);

    List<SewingCraftVo> searchSewingCraftDataByParams(@Param("params") String params, @Param("clothingCategoryCode") String clothingCategoryCode);

    int updateCraftFlowNum(@Param("workplaceCraftCode") String workplaceCraftCode, @Param("craftFlowNum") Integer craftFlowNum);
}
