package com.ylzs.dao.bigstylerecord;

import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-09-16 11:16
 */
public interface TimePriceOperationLogDao {
    public void insertData(TimePriceOperationLog log);

    public void insertOrderData(TimePriceOperationLog log);

    public List<TimePriceOperationLog> getDataByBusinessTypeAndCode(@Param("tableName") String tableName,@Param("code") String code, @Param("businessType") Integer businessType);
}
