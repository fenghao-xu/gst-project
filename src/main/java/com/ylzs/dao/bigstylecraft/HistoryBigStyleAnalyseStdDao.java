package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftStd;

public interface HistoryBigStyleAnalyseStdDao {
    int deleteByPrimaryKey(Long id);

    int insert(SewingCraftStd record);

    int insertSelective(SewingCraftStd record);

    SewingCraftStd selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SewingCraftStd record);

    int updateByPrimaryKey(SewingCraftStd record);
}