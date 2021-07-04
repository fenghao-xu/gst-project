package com.ylzs.entity.plm;

import lombok.Data;
import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataMaterialMarkers {
    private List<CutParameterMasterDataMarkerTables> markerTables;//床次信息列表（数组）
}
