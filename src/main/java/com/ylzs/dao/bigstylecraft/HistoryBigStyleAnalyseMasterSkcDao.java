package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalyseSkc;

public interface HistoryBigStyleAnalyseMasterSkcDao {
    int deleteByPrimaryKey(Long id);

    int insert(BigStyleAnalyseSkc record);

    int insertSelective(BigStyleAnalyseSkc record);

    BigStyleAnalyseSkc selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BigStyleAnalyseSkc record);

    int updateByPrimaryKey(BigStyleAnalyseSkc record);
}