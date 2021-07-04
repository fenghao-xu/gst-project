package com.ylzs.service.fabricProperty;

import com.ylzs.entity.fabricProperty.FabricPropertyData;
import com.ylzs.entity.fabricProperty.resp.FabricPropertyDataResp;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 材料属性值接口
 * @Date 2020/3/9
 */
public interface IFabricPropertyDataService extends IOriginService<FabricPropertyData> {

    /**
     * 获取材料属性值
     * @param fabricPropertyCode  材料属性编码
     * @param propertyValue 属性值
     * @return
     */
    List<FabricPropertyDataResp> selectPropertyDataList(String fabricPropertyCode, String propertyValue,String kindCode);

    /**
     * 根据属性获取材料属性值
     * @return
     */
    List<FabricPropertyDataResp> selectMateriLCodePropertyDataList(String materialCode);


}
