package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PICustomOrderPart;
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
public interface PICustomOrderPartDao extends BaseDAO<PICustomOrderPart> {

    void addCustomOrderPartList(List<PICustomOrderPart> piCustomOrderParList);


    List<PICustomOrderPart> getOrderAll(@Param("orderId") String orderId, @Param("orderLineId")String orderLineId);
}
