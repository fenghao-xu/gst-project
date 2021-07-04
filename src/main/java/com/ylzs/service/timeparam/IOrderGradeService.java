package com.ylzs.service.timeparam;


import com.ylzs.entity.timeparam.OrderGrade;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * 订单等级数据
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-01 15:47:56
 */
public interface IOrderGradeService extends IOriginService<OrderGrade> {
    public List<OrderGrade> getAllOrderGrade();
}

