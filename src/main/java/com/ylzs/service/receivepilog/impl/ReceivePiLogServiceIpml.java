package com.ylzs.service.receivepilog.impl;

import com.ylzs.dao.receivepilog.ReceivePiLogDao;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:32 2020/3/7
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReceivePiLogServiceIpml implements IReceivePiLogService {

    @Resource
    private ReceivePiLogDao receivePiLogDao;

    @Override
    public int add(ReceivePiLog receivePiLog) {
        return receivePiLogDao.add(receivePiLog);
    }

    @Override
    public List<ReceivePiLog> getList(Map<String, String> params) throws Exception {
        return receivePiLogDao.getList(params);
    }

    @Override
    public int updateCount(Long id, Integer count) {
        return receivePiLogDao.updateCount(id, count);
    }
}
