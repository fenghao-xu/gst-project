package com.ylzs.service.aps.impl;

import com.ylzs.dao.aps.CappPiPreScheduleResultMapper;
import com.ylzs.entity.aps.CappPiPreScheduleResult;
import com.ylzs.service.aps.IApsReceiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ApsReceiveService implements IApsReceiveService {
    @Resource
    CappPiPreScheduleResultMapper cappPiPreScheduleResultMapper;

    @Override
    public int addPreScheduleResult(CappPiPreScheduleResult preScheduleResult) {
        return cappPiPreScheduleResultMapper.insert(preScheduleResult);
    }
}
