package com.ylzs.service.bigstylerecord.impl;

import com.ylzs.dao.bigstylerecord.TimePriceOperationLogDao;
import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import com.ylzs.service.bigstylerecord.ITimePriceOperationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-09-16 11:31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TimePriceOperationServiceImpl implements ITimePriceOperationService {
    @Resource
    private TimePriceOperationLogDao timePriceOperationLogDao;

    @Override
    public void insertData(TimePriceOperationLog log) {
        timePriceOperationLogDao.insertData(log);
    }

    @Override
    public List<TimePriceOperationLog> getDataByBusinessTypeAndCode(String tableName,String code, Integer businessType) {
        return timePriceOperationLogDao.getDataByBusinessTypeAndCode(tableName,code, businessType);
    }
}
