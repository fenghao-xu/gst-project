package com.ylzs.service.receivepilog;

import com.ylzs.entity.receivepilog.ReceivePiLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:PI主数据接收
 * @Date: Created in 10:31 2020/3/7
 */
public interface IReceivePiLogService {
    int add(ReceivePiLog receivePiLog);

    List<ReceivePiLog> getList(Map<String, String> params) throws Exception;

    int updateCount(Long id, Integer count);
}
