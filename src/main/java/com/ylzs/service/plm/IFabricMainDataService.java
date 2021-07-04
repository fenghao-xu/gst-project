package com.ylzs.service.plm;

import com.ylzs.entity.plm.FabricMainData;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:PI材料主数据
 * @Date: Created in 15:23 2020/3/11
 */
public interface IFabricMainDataService {

    void addOrUpdateFabricDataDao(FabricMainData fabricMainData);

    List<FabricMainData> getAllFabricData();

    List<FabricMainData> getFabricMainDataByCode(String mainMaterialCode);

}
