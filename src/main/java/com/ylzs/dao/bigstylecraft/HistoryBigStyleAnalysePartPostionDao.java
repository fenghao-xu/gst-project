package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;

public interface HistoryBigStyleAnalysePartPostionDao {
    int deleteByPrimaryKey(Long id);

    int insert(SewingCraftPartPosition record);

    int insertSelective(SewingCraftPartPosition record);

    SewingCraftPartPosition selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SewingCraftPartPosition record);

    int updateByPrimaryKey(SewingCraftPartPosition record);
}