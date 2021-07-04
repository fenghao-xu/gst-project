package com.ylzs.dao.fabricProperty;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.fabricProperty.FabricPropertyData;
import com.ylzs.entity.fabricProperty.resp.FabricPropertyDataResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * @author weikang
 * @Description
 * @Date 2020/3/9
 */
@Mapper
public interface FabricPropertyDataDao extends BaseDAO<FabricPropertyData> {


    List<FabricPropertyDataResp> selectPropertyDataList(Map<String, String> map);

    @Select("SELECT fabric_property_code AS fabricPropertyCode, " +
            "fabric_property_name AS fabricPropertyName," +
            "property_value_code AS propertyValueCode ,property_value AS propertyValue " +
            "FROM fabric_property_data WHERE status = 1020 AND material_craft_kind_code = #{materialCode} ")
    List<FabricPropertyDataResp> selectMateriLCodePropertyDataList(String materialCode);

}
