package com.ylzs.service.bigstylecraft;

import com.ylzs.dao.bigstylecraft.BigStyleOperationLogDao;
import com.ylzs.entity.bigstylecraft.BigStyleOperationLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author xuwei
 * @create 2020-08-05 8:55
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleOperationLogService {

    @Resource
    private BigStyleOperationLogDao bigStyleOperationLogDao;

    public void insertData(BigStyleOperationLog log) {
        bigStyleOperationLogDao.insertData(log);
    }
}
