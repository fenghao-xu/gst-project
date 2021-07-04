package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;

public interface HistoryBigStyleAnalyseWorkplaceDao {
    int deleteByPrimaryKey(Long id);

    int insert(SewingCraftWarehouseWorkplace record);

    int insertSelective(SewingCraftWarehouseWorkplace record);

    SewingCraftWarehouseWorkplace selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SewingCraftWarehouseWorkplace record);

    int updateByPrimaryKey(SewingCraftWarehouseWorkplace record);
}