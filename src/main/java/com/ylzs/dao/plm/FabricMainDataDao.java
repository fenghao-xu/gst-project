package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.FabricMainData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:54 2020/3/11
 */
@Mapper
public interface FabricMainDataDao extends BaseDAO<FabricMainData> {

    void addOrUpdateFabricDataDao(FabricMainData fabricMainData);

    List<FabricMainData> getAllFabricData();

    List<FabricMainData> getFabricMainDataByCode(@Param("mainMaterialCode") String mainMaterialCode);
}
