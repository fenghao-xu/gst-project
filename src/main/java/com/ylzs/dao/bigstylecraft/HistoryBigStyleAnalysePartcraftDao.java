package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;

public interface HistoryBigStyleAnalysePartcraftDao {
    int deleteByPrimaryKey(Long id);

    int insert(BigStyleAnalysePartCraft record);

    int insertSelective(BigStyleAnalysePartCraft record);

    BigStyleAnalysePartCraft selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BigStyleAnalysePartCraft record);

    int updateByPrimaryKey(BigStyleAnalysePartCraft record);
}