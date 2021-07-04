package com.ylzs.service.bigstylerecord;

import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-09-16 11:29
 */
public interface ITimePriceOperationService {
    public void insertData(TimePriceOperationLog log);

    public List<TimePriceOperationLog> getDataByBusinessTypeAndCode(String tableName, String code,  Integer businessType);
}
