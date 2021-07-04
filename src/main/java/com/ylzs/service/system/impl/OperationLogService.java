package com.ylzs.service.system.impl;

import com.ylzs.dao.system.OperationLogDao;
import com.ylzs.entity.system.OperationLog;
import com.ylzs.service.system.IOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 说明：操作日志服务
 *
 * @author Administrator
 * 2019-09-30 11:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OperationLogService implements IOperationLogService {
    @Resource
    private OperationLogDao operationLogDao;



    @Override
    public Integer addOperationLog(OperationLog operationLog) {
        return operationLogDao.addOperationLog(operationLog);
    }

    @Override
    public Integer deleteOperationLog(long id) {
        return operationLogDao.deleteOperationLog(id);

    }

    @Override
    public Integer updateOperationLog(OperationLog operationLog) {
        return operationLogDao.updateOperationLog(operationLog);
    }

    @Override
    public List<OperationLog> getOperationLogById(long[] ids) {
        return operationLogDao.getOperationLogById(ids);
    }

    @Override
    public List<OperationLog> getOperationLogByPage(String keywords, Date beginDate, Date endDate, String[] moduleCodes, String[] operCodes) {
        return operationLogDao.getOperationLogByPage(keywords, beginDate, endDate, moduleCodes, operCodes);
    }

    @Override
    public void addLog(String moduleCode, ActionType actionType, String userCode, String desc) {
        addLog(moduleCode, actionType, userCode, desc, null);
    }


    @Override
    public void addLog(String moduleCode, ActionType actionType, String userCode, String desc, String ip) {
        String operCode = "";
        switch (actionType) {
            case CREATE:
                operCode = "C";
                break;
            case READ:
                operCode = "R";
                break;
            case UPDATE:
                operCode = "U";
                break;
            case DELETE:
                operCode = "D";
                break;
            case LOGIN:
                operCode = "I";
                break;
            case LOGOUT:
                operCode = "O";
                break;
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setModuleCode(moduleCode);
        operationLog.setOperCode(operCode);
        operationLog.setOperDesc(desc);
        operationLog.setUserCode(userCode);
        operationLog.setCreateTime(new Date());
        operationLog.setIp(ip);
        operationLogDao.addOperationLog(operationLog);

    }
}
