package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;

public interface HistoryBigStyleAnalyseSewingCraftDao {
    int deleteByPrimaryKey(Long id);

    int insert(SewingCraftWarehouse record);

    int insertSelective(SewingCraftWarehouse record);

    SewingCraftWarehouse selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SewingCraftWarehouse record);

    int updateByPrimaryKey(SewingCraftWarehouse record);
}