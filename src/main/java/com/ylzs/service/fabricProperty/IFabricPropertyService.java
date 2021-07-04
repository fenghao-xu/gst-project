package com.ylzs.service.fabricProperty;

import com.ylzs.common.util.Result;
import com.ylzs.entity.fabricProperty.FabricProperty;
import com.ylzs.service.IOriginService;

/**
 * @author weikang
 * @Description 材料属性接口
 * @Date 2020/3/9
 */
public interface IFabricPropertyService extends IOriginService<FabricProperty> {

    /**
     * 通过材料类型编码获取材料属性值
     * @param kindCode
     * @return
     */
    Result getFabricProperty(String kindCode);
}
