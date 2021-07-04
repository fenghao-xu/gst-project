package com.ylzs.service.plm.cutParameter;


import com.ylzs.entity.plm.CutParameterMasterDataMarkerTablesSizes;

import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
public interface ICutParameterMasterDataMarketTablesSizesService {
    int addOrUpdateCutParameterMasterDataMarkerTablesSizes(List<CutParameterMasterDataMarkerTablesSizes> cutParameterMasterDataMarkerTablesSizes);
    int addOrUpdateCutParameterMasterDataMarkerTablesSizes2(CutParameterMasterDataMarkerTablesSizes cutParameterMasterDataMarkerTablesSizes);
}
