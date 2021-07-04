package com.ylzs.dao.mes;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.mes.PartPartMiddleConfig;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PartPartMiddleConfigDao extends BaseDAO<PartPartMiddleConfig> {
    @MapKey("partCode")
    Map<String, PartPartMiddleConfig> getPartPartMiddleConfigMap(@Param("craftCategoryCode") String craftCategoryCode, @Param("clothesCategoryCode")  String clothesCategoryCode);
}