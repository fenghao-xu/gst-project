package com.ylzs.service.plm;

import com.ylzs.entity.plm.PICustomOrderPart;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:30 2020/3/19
 */
public interface IPICustomOrderPartService {


    void addCustomOrderPartList(List<PICustomOrderPart> piCustomOrderParList);

    List<PICustomOrderPart> getOrderAll(String orderId,String orderLineId);





}
