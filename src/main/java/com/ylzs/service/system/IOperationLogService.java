package com.ylzs.service.system;

import com.ylzs.entity.system.OperationLog;

import java.util.Date;
import java.util.List;

/**
 * 说明：操作日志服务接口
 *
 * @author Administrator
 * 2019-09-30 11:25
 */
public interface IOperationLogService {
    enum ActionType {CREATE,READ,UPDATE,DELETE,LOGIN,LOGOUT}

    Integer addOperationLog(OperationLog operationLog);
    Integer deleteOperationLog(long id);
    Integer updateOperationLog(OperationLog operationLog);
    List<OperationLog> getOperationLogById(long[] ids);
    List<OperationLog> getOperationLogByPage(String keywords, Date beginDate,
                                             Date endDate, String[] moduleCodes, String[] operCodes);

    void addLog(String moduleCode, ActionType actionType, String userCode, String desc, String ip);
    void addLog(String moduleCode, ActionType actionType, String userCode, String desc);
}


