package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftAction;

public interface HistoryBigStyleAnalyseMotionDao {
    int deleteByPrimaryKey(Long id);

    int insert(SewingCraftAction record);

    int insertSelective(SewingCraftAction record);

    SewingCraftAction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SewingCraftAction record);

    int updateByPrimaryKey(SewingCraftAction record);
}