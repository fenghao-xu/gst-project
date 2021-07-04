package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;

public interface HistoryBigStyleAnalysePartcraftDetailDao {
    int deleteByPrimaryKey(Long id);

    int insert(BigStyleAnalysePartCraftDetail record);

    int insertSelective(BigStyleAnalysePartCraftDetail record);

    BigStyleAnalysePartCraftDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BigStyleAnalysePartCraftDetail record);

    int updateByPrimaryKey(BigStyleAnalysePartCraftDetail record);
}