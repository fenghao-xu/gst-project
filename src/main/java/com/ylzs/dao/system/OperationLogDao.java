package com.ylzs.dao.system;

import com.ylzs.entity.system.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-26 10:31
 */
public interface OperationLogDao {
    Integer addOperationLog(OperationLog operationLog);
    Integer deleteOperationLog(long id);
    Integer updateOperationLog(OperationLog operationLog);
    List<OperationLog> getOperationLogById(@Param("ids") long[] ids);
    List<OperationLog> getOperationLogByPage(@Param("keywords") String keywords,
                                             @Param("beginDate") Date beginDate,
                                             @Param("endDate") Date endDate,
                                             @Param("moduleCodes") String[] moduleCodes,
                                             @Param("operCodes") String[] operCodes);
}
