package com.ylzs.dao.orderprocessing;

import com.ylzs.entity.orderprocessing.OrderProcessingStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-07-18 9:27
 */
public interface OrderProcessingStatusDao {
    public void insertOrderProcessingStatusList(@Param("orderList") List<OrderProcessingStatus> orderList);

    public List<OrderProcessingStatus> getDataByParam(Map<String, Object> param);

    public  void addOrUpdate(OrderProcessingStatus orderProcessingStatus);
}
