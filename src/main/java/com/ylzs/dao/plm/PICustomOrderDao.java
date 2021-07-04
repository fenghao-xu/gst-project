package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PICustomOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:12 2020/3/19
 */
@Mapper
public interface PICustomOrderDao extends BaseDAO<PICustomOrder> {


    void addCustomOrder(List<PICustomOrder> piCustomOrderList);

    List<PICustomOrder> selOrderById(@Param("orderId") String orderId,@Param("orderLineId") String orderLineId);


    PICustomOrder getOrderId(@Param("orderId") String orderId,@Param("orderLineId") String orderLineId);

    @Select("SELECT * FROM capp_pi_custom_order")
    List<PICustomOrder> getOrderList();

    List<PICustomOrder> getCustomOrderInvalid(@Param("styleCode") String styleCode);

    List<PICustomOrder> getCustomOrderInvalidByRandomCode(@Param("partCraftMainRandomCode") Long partCraftMainRandomCode);
}
