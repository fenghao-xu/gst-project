package com.ylzs.dao.receivepilog;

import com.ylzs.entity.receivepilog.ReceivePiLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:21 2020/3/7
 */
public interface ReceivePiLogDao {
    public int add(ReceivePiLog receivePiLog);

    List<ReceivePiLog> getList(Map<String, String> params) throws Exception;

    int updateCount(@Param("id") Long id, @Param("count") Integer count);
}
