package com.ylzs.service.orderprocessing;

import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.dao.orderprocessing.OrderProcessingStatusDao;
import com.ylzs.entity.orderprocessing.OrderProcessingStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-07-18 9:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderProcessingStatusService {

    @Resource
    private OrderProcessingStatusDao orderProcessingStatusDao;

    public void insertOrderProcessingStatusList(List<OrderProcessingStatus> orderList) {
        orderProcessingStatusDao.insertOrderProcessingStatusList(orderList);
    }

    public List<OrderProcessingStatus> getDataByParam(Map<String, Object> param) {
        return orderProcessingStatusDao.getDataByParam(param);
    }

    public void addOrUpdate(OrderProcessingStatus orderProcessingStatus) {
        orderProcessingStatusDao.addOrUpdate(orderProcessingStatus);
    }

    public void addOrUpdate(String orderNo, String processingStatusName, Integer processingStatus, String createUser, String updateUser) {
        OrderProcessingStatus status = new OrderProcessingStatus();
        status.setProcessingStatus(processingStatus);
        status.setProcessingStatusName(processingStatusName);
        status.setCreateUser(createUser);
        status.setUpdateUser(updateUser);
        status.setOrderNo(orderNo);
        status.setCreateTime(new Date());
        status.setUpdateTime(new Date());
        orderProcessingStatusDao.addOrUpdate(status);
    }
}
