package com.ylzs.dao.timeparam;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.timeparam.OrderGrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单等级数据
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-01 15:47:56
 */
@Mapper
public interface OrderGradeDao extends BaseDAO<OrderGrade> {
	public List<OrderGrade>getAllOrderGrade();
}
