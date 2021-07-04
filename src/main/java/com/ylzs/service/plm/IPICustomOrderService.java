package com.ylzs.service.plm;

import com.ylzs.entity.plm.PICustomOrder;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:28 2020/3/19
 */
public interface IPICustomOrderService {

    void addCustomOrder(List<PICustomOrder> piCustomOrderList);

    List<PICustomOrder> selOrderById(String orderId,String orderLineId);

    PICustomOrder getOrderId(String orderId,String orderLineId);
    //获取未生产订制款的订单
    List<PICustomOrder> getCustomOrderInvalid(String styleCode);
    List<PICustomOrder> getCustomOrderInvalidByRandomCode(Long partCraftMainRandomCode);

}