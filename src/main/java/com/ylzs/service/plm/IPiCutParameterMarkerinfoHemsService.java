package com.ylzs.service.plm;

import com.ylzs.entity.plm.PiCutParameterMarkerinfoHems;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @className IPiCutParameterMarkerinfoHemsService
 * @Description
 * @Author sky
 * @create 2020-03-20 10:06:23
 */
public interface IPiCutParameterMarkerinfoHemsService extends IOriginService<PiCutParameterMarkerinfoHems> {

    /**
     * 根据订单号查询CAD定制工艺信息
     * @param orderId
     * @return
     */
    List<PiCutParameterMarkerinfoHems> getOrderMarkerInfoHemosAll(String orderId,String orderLineId);
}
