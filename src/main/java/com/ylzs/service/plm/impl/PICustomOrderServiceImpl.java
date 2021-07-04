package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.PICustomOrderDao;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.service.plm.IPICustomOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:29 2020/3/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PICustomOrderServiceImpl implements IPICustomOrderService {


    @Resource
    private PICustomOrderDao piCustomOrderDao;

    @Override
    public void addCustomOrder(List<PICustomOrder> piCustomOrderList) {
        piCustomOrderDao.addCustomOrder(piCustomOrderList);
    }

    @Override
    public List<PICustomOrder> selOrderById(String orderId, String orderLineId) {
        return piCustomOrderDao.selOrderById(orderId,orderLineId);
    }

    @Override
    public PICustomOrder getOrderId(String orderId, String orderLineId) {
        return piCustomOrderDao.getOrderId(orderId,orderLineId);
    }

    @Override
    public List<PICustomOrder> getCustomOrderInvalid(String styleCode) {
        return piCustomOrderDao.getCustomOrderInvalid(styleCode);
    }

    @Override
    public List<PICustomOrder> getCustomOrderInvalidByRandomCode(Long partCraftMainRandomCode) {
        return piCustomOrderDao.getCustomOrderInvalidByRandomCode(partCraftMainRandomCode);
    }

}
