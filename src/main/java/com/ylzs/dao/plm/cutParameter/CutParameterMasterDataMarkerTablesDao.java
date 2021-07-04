package com.ylzs.dao.plm.cutParameter;


import com.ylzs.entity.plm.CutParameterMasterDataMarkerTables;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Mapper
public interface CutParameterMasterDataMarkerTablesDao {
    int addOrUpdateCutParameterMasterDataMarkerTables(List<CutParameterMasterDataMarkerTables> cutParameterMasterDataMarkerTables);
}
