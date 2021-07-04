package com.ylzs.dao.aps;

import com.ylzs.entity.aps.CappPiPreScheduleResult;

public interface CappPiPreScheduleResultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CappPiPreScheduleResult record);

    int insertSelective(CappPiPreScheduleResult record);

    CappPiPreScheduleResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CappPiPreScheduleResult record);

    int updateByPrimaryKey(CappPiPreScheduleResult record);
}