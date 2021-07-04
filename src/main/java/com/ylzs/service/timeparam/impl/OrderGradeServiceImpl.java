package com.ylzs.service.timeparam.impl;


import com.ylzs.dao.timeparam.OrderGradeDao;
import com.ylzs.entity.timeparam.OrderGrade;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.timeparam.IOrderGradeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service()
public class OrderGradeServiceImpl extends OriginServiceImpl<OrderGradeDao, OrderGrade> implements IOrderGradeService {

    @Resource
    private OrderGradeDao orderGradeDao;

    @Override
    public List<OrderGrade> getAllOrderGrade() {
        return orderGradeDao.getAllOrderGrade();
    }
}